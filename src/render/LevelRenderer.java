package render;

import entities.Door;
import entities.Player;
import graphics.ScreenSlice;
import graphics.Texture;
import interfaces.ILightSource;
import interfaces.IRenderable;
import level.Level;
import level.Tile;
import utils.math.*;
import utils.render.RenderContext;
import utils.render.TextureManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;


public class LevelRenderer {

    private Screen screen;
    private Level level;
    private double[] depthBuffer;
    private Vector2d[] pixWorldPos;

    private Camera camera;
    private Graphics2D g2d;
    private MapIntersectionFinder interFinder;

    private int screenWidth;
    private int screenHeight;
    private int[] screenPixels;

    private static double wallHeight = 1.0; //in screen coords - wall has height of the screen height
    private List<Intersection> intersections;
    private List<PriorityQueue<ScreenSlice>> screenSlices;

    public LevelRenderer(Level level, Screen screen, Graphics2D g2d, Camera camera) {
        this.screen = screen;
        this.level = level;
        this.camera = camera;
        this.g2d = screen.getGraphics();

        this.screenHeight = screen.getHeight();
        this.screenWidth = screen.getWidth();
        this.screenPixels = screen.getPixels();
        this.depthBuffer = new double[screenWidth * screenHeight];

        this.pixWorldPos = new Vector2d[screenWidth * screenHeight];
        for(int i = 0; i < this.pixWorldPos.length; i++) {
            this.pixWorldPos[i] = new Vector2d();
        }

        this.screenSlices = new ArrayList<>(this.screenWidth);
        this.intersections = new LinkedList<>();
        for(int i = 0; i < screenWidth; i++) {
            this.screenSlices.add(i,new PriorityQueue<>((ScreenSlice p1,ScreenSlice p2) -> -Double.compare(p1.distanceToSlice, p2.distanceToSlice)));
        }

        this.interFinder = new MapIntersectionFinder(level);
    }

    public void renderLevel(double dt) {
        var lightSources = level.getLightSources();

        Arrays.fill(depthBuffer, Double.MAX_VALUE);
        renderBackground();

        for(int col = 0; col < screenWidth; col++) {
            int finalCol = col;
            double cameraX = 2.0 * (double)(col) / (double)(screenWidth) - 1; //x-coordinate in camera space
            Ray ray = MathUtils.constructRay(this.camera, cameraX);
            intersections.clear();

            interFinder.findAllIntersections(ray, this.camera.getAngle(), intersections);
            screenSlices.get(col).addAll(intersections.stream().map(i -> transformIntersectionToScreenSlice(i, finalCol, ray)).toList());
            var currSlices = screenSlices.get(col);
            var currSlice = currSlices.peek();

            if(currSlice == null) {
                System.out.println("SLICE IS NULL!!!!");
                continue;
            }
            drawFloorAndCeiling(col, ray, currSlice.drawStartY + currSlice.sliceHeight, lightSources);
        }

        renderEntities(screenWidth, screenHeight, screenPixels, lightSources);

        for(int i = 0; i < screenWidth; i++) {
            var currSlices = screenSlices.get(i);
            var currSlice = currSlices.poll();
            while(currSlice != null) {
                renderSlice(currSlice);
                currSlice = currSlices.poll();
            }
        }

        renderPlayer(level.getPlayer());
        renderGUI(level.getPlayer());
    }


    public void drawFloorAndCeiling(int col, Ray ray, int endY, List<ILightSource> lightSources) {

        for(int y = endY; y < screenHeight; y++) {
            double screenY = ((double)y / (double)screenHeight) * 2.0 - 1.0;
            double distance = wallHeight * (camera.distFromPlane / screenY);
            double mapPositionX = ray.getOrigin().x + ray.getDirection().x * distance;
            double mapPositionY = ray.getOrigin().y - ray.getDirection().y * distance;

            Texture floor = level.getMapInfo().getTileAt((int)mapPositionX, (int)mapPositionY).floorTexture;
            Texture ceiling = level.getMapInfo().getTileAt((int)mapPositionX, (int)mapPositionY).ceilingTexture;

            int textureX = (int)((mapPositionX - (int)mapPositionX) * floor.getWidth());
            int textureY = (int)((mapPositionY - (int)mapPositionY) * floor.getHeight());

            int fColor = floor.getPixelAt(textureX, textureY);
            int cColor = ceiling.getPixelAt(textureX, textureY);
            int fPos = col + y * screenWidth;
            int cPos = col + (screenHeight - y) * screenWidth;

            pixWorldPos[cPos].setXY(mapPositionX, mapPositionY);
            depthBuffer[cPos] = distance;
            screenPixels[cPos] = applyLightsToPixelAndBlend(pixWorldPos[cPos], cColor, screenPixels[cPos], level.getLightSources());

            pixWorldPos[fPos].setXY(mapPositionX, mapPositionY);
            depthBuffer[fPos] = distance;
            screenPixels[fPos] = applyLightsToPixelAndBlend(pixWorldPos[fPos], fColor, screenPixels[fPos], level.getLightSources());
        }

    }

    private void renderEntities(int screenWidth, int screenHeight, int[] screenPixels, List<ILightSource> lightSrcs) {
        List<IRenderable> entities = level.getRenderables();
        entities.sort(Comparator.comparingDouble((IRenderable r) -> MathUtils.getSimpleDistance(r.getRenderContext().getPosition(), camera.position)).reversed());
        entities.forEach((entity) -> drawSprite(screenWidth, screenHeight, screenPixels, entity, lightSrcs));
    }

    private int applyLightsToPixel(Vector2d pixelWorldPos, int pixel, List<ILightSource> lightSources) {
        float ambientStrength = 0.35f;

        int currR = ((pixel & 0x00ff0000) >> 16);
        int currG = ((pixel & 0x0000ff00) >> 8);
        int currB = (pixel & 0x000000ff);

        //apply diffuse
        double ambientRed = ((ambientStrength * 0.3));
        double ambientGreen = ((ambientStrength * 0.3));
        double ambientBlue = ((ambientStrength * 0.4));

        double lightR = 0.0f;
        double lightG = 0.0f;
        double lightB = 0.0f;

        for (ILightSource lightSrc: lightSources) {
            var props = lightSrc.getLightSourceProperty();
            double distLightToInter = MathUtils.getSimpleDistance(pixelWorldPos, props.worldPosition);
            double attentuation = 1.0 / (1.0 + props.attentuation * Math.pow(distLightToInter, 2));

            //ARGB
            lightR += attentuation * props.red;
            lightG += attentuation * props.green;
            lightB += attentuation * props.blue;
            lightR = MathUtils.clamp(lightR, 1.00f,0.00f);
            lightG = MathUtils.clamp(lightG, 1.00f,0.00f);
            lightB = MathUtils.clamp(lightB, 1.00f,0.00f);
        }

        int red = ((int)((ambientRed + lightR) * currR));
        int green = ((int)((ambientGreen + lightG) * currG));
        int blue = ((int)((ambientBlue + lightB) * currB));

        return (pixel & 0xff000000) | ((red << 16) | (green << 8) | blue);
    }

    private int applyLightsToPixel(int pixel, double[] computedLight) {
        int currR = ((pixel & 0x00ff0000) >> 16);
        int currG = ((pixel & 0x0000ff00) >> 8);
        int currB = (pixel & 0x000000ff);


        currR = (int)(currR * computedLight[0]);
        currG = (int)(currG * computedLight[1]);
        currB = (int)(currB * computedLight[2]);
        return (pixel & 0xff000000) | ((currR << 16) | (currG << 8) | currB);
    }


    private int applyLightsToPixelAndBlend(Vector2d pixelWorldPos, int pixel, int oldPix, List<ILightSource> lightSources) {
        int lightedPix = applyLightsToPixel(pixelWorldPos, pixel, lightSources);
        return MathUtils.blendPixel(lightedPix, oldPix);
    }

    private int applyLightsToPixelAndBlend(int pixel, int oldPix, double[] lights) {
        int lightedPix = applyLightsToPixel(pixel, lights);
        return MathUtils.blendPixel(lightedPix, oldPix);
    }


    private void drawSprite(int screenWidth, int screenHeight, int[] screenPixels, IRenderable entity, List<ILightSource> lightSrcs) {
        RenderContext context = entity.getRenderContext();
        Vector2d relativePos = MathUtils.getRelativePos(context.getPosition(), camera.position);
        double angle = MathUtils.getAngle(camera.position, entity.getRenderContext().getPosition());
        var tex = context.getTexture(angle);

        //transform sprite with the inverse camera matrix
        double invDet = 1.0f / (camera.plane.x * camera.direction.y - camera.direction.x * camera.plane.y); //required for correct matrix multiplication
        double transformX = invDet * (camera.direction.y * relativePos.x - camera.direction.x * relativePos.y);
        double transformY = invDet * (- camera.plane.y * relativePos.x + camera.plane.x * relativePos.y); //this is actually the depth inside the screen, that what Z is in 3D

        int spriteScreenX = (int)((screenWidth / 2) * (1 + transformX / transformY));

        int spriteHeight = Math.abs((int)Math.round(screenHeight  / transformY)); //using 'transformY' instead of the real distance prevents fisheye
        double aspectRatio = (double)tex.getHeight() / (double)tex.getWidth();
        int spriteWidth = (int)(spriteHeight / aspectRatio);

        int drawStartX = -spriteWidth / 2 + spriteScreenX;
        int drawStartY = (int)( - (double)spriteHeight / 2.0f + ((double)screenHeight / 2.0f));

        if(drawStartX > screenWidth || drawStartX + spriteWidth < 0)
            return;
        if(drawStartY > screenHeight || drawStartY + spriteHeight < 0)
            return;

        for(int stripe = 0; stripe < spriteWidth; stripe++)
        {
            int startStripeX = stripe + drawStartX;
            if(startStripeX < 0 || startStripeX >= screenWidth)
                continue;
            int texX = (int)((double)(stripe) / spriteWidth * tex.getWidth());
            if(transformY > 0 ) {
                ScreenSlice slice = new ScreenSlice();
                slice.sliceHeight = spriteHeight;
                slice.distanceToSlice = transformY;
                slice.drawStartX = startStripeX;
                slice.drawStartY = drawStartY;
                slice.texture = tex;
                slice.texOffsetX = texX;
                slice.worldPosition = context.getPosition();
                slice.normalX = Math.cos(angle);
                slice.normalY = Math.sin(angle);
                slice.name = entity.getClass().getSimpleName();
                insertSpriteSlice(slice);
            }
        }
    }

    public void renderPlayer(Player player) {
        BufferedImage tex = player.getWeaponTexture().getImage();
        int x = this.screen.getWidth() / 2 - tex.getWidth();
        int y = this.screen.getHeight() - tex.getHeight() * 3;
        g2d.drawImage(tex, x, y, tex.getWidth() * 3, tex.getHeight() * 3, null);
    }


    public void renderGUI(Player player) {
        g2d.drawString("Ammo: " + player.getWeapon().getAmmo(), 10, 10);
    }

    public void renderBackground() {
        g2d.drawImage(TextureManager.getInstance().getTextureByName("background").getImage(),
                0,0, this.screen.getWidth(), this.screen.getHeight(),null);
    }

    private double[] computeLightForScreenSlice(ScreenSlice slice, List<ILightSource> lightSources) {
        double[] finalLight = new double[3];

        float ambientStrength = 0.35f;

        //apply diffuse
        double ambientRed = ((ambientStrength * 0.3));
        double ambientGreen = ((ambientStrength * 0.3));
        double ambientBlue = ((ambientStrength * 0.4));

        double lightR = 0.0f;
        double lightG = 0.0f;
        double lightB = 0.0f;

        for (ILightSource lightSrc: lightSources) {
            var props = lightSrc.getLightSourceProperty();
            double dot = MathUtils.dotProduct2D(slice.worldPosition, lightSrc.getLightSourceProperty().worldPosition);
            //if it is behind do not compute
            if(dot < 0)
                continue;

            double distLightToInter = MathUtils.getSimpleDistance(slice.worldPosition, props.worldPosition);
            //if it is too far away, do not try
            if(distLightToInter > 15.5d)
                continue;

            double attentuation = 1.0 / (1.0 + props.attentuation * Math.pow(distLightToInter, 2));

            //ARGB
            lightR += attentuation * props.red;
            lightG += attentuation * props.green;
            lightB += attentuation * props.blue;
            lightR = MathUtils.clamp(lightR, 1.0f,0.0f);
            lightG = MathUtils.clamp(lightG, 1.0f,0.0f);
            lightB = MathUtils.clamp(lightB, 1.0f,0.0f);
        }

        double red = ambientRed + lightR;
        double green = ambientGreen + lightG;
        double blue = ambientBlue + lightB;

        //clamp values r|g|b
        finalLight[0] = MathUtils.clamp(red, 1.0,0.0);
        finalLight[1] = MathUtils.clamp(green, 1.0,0.0);
        finalLight[2] = MathUtils.clamp(blue, 1.0,0.0);


        return finalLight;
    }

    private ScreenSlice transformIntersectionToScreenSlice(Intersection i, int currColumn, Ray currRay) {
        ScreenSlice sl = new ScreenSlice();
        switch (i.wallCollisionSide) {
            case Intersection.DOWN_SIDE -> {
                sl.normalX = 0.0d;
                sl.normalY = -1.0d;
            }
            case Intersection.UP_SIDE -> {
                sl.normalX = 0.0d;
                sl.normalY = 1.0d;
            }
            case Intersection.RIGHT_SIDE -> {
                sl.normalX = 1.0d;
                sl.normalY = 0.0d;
            }
            case Intersection.LEFT_SIDE -> {
                sl.normalX = -1.0d;
                sl.normalY = 0.0d;
            }
        }

        double projectedHeight = Math.round(((screenHeight * i.t.height) / i.distToWall));
        int startY = (int)(screenHeight / 2.0f - (projectedHeight / 2.0f));
//        if(startY < 0) startY = 0;

        sl.sliceHeight = (int)projectedHeight;
        sl.drawStartY = startY;
        sl.drawStartX = currColumn;
        sl.distanceToSlice = i.distToWall;
        sl.texture = i.t.wallTexture;

        //compute texture offsets
        double wallX = i.isVertical ? i.position.y : i.position.x;
        if(i.isVertical) {
            wallX = wallX - Math.floor(i.position.y);
        } else {
            if(i.wallType == Tile.HORIZ_DOOR) {
                if(!currRay.isFacingUp()) {
                    wallX -= Math.floor(i.position.x);
                    wallX = 1.0f - wallX - level.getDoor((int)i.position.x, (int)i.position.y).getOffset();
                } else {
                    Door d = level.getDoor((int)i.position.x, (int)i.position.y);
                    wallX -= Math.floor(i.position.x) - d.getOffset();
                }
            }
            else {
                wallX -= Math.floor(i.position.x);
            }
        }

        int texelX = (int) (wallX * (i.t.wallTexture.getWidth()));

        if((i.isVertical && !currRay.isFacingRight()) || (!i.isVertical && !currRay.isFacingUp())) {
            texelX = i.t.wallTexture.getWidth() - texelX - 1;
        }

        sl.texOffsetX = texelX;

        sl.worldPosition = i.position;
        sl.name = "wall";
        return sl;
    }


    private void renderSlice(ScreenSlice sl) {
        var texture = sl.texture;
        var light = computeLightForScreenSlice(sl, level.getLightSources());
        for(int row = 0; row < sl.sliceHeight; row++) {
            int screenRow = sl.drawStartY + row;

            if(MathUtils.isInBounds(sl.drawStartX, screenRow, screenWidth, screenHeight)) {
                int screenPos = sl.drawStartX + screenRow * screenWidth;
                int tY = (int)(((double)row / (double)sl.sliceHeight) * (double)texture.getHeight());
                int oldPix = screenPixels[screenPos];
                int currPixel = texture.getPixelAt(sl.texOffsetX, tY);
                if(((currPixel >> 24) & 0xff) != 0 && sl.distanceToSlice < this.depthBuffer[screenPos]) {
                    this.depthBuffer[screenPos] = sl.distanceToSlice;
                    this.pixWorldPos[screenPos].setXY(sl.worldPosition.x, sl.worldPosition.y);
                    screenPixels[screenPos] = applyLightsToPixelAndBlend(currPixel, oldPix, light);
                }
            }
        }

    }

    private void insertSpriteSlice(ScreenSlice s) {
//        if(s.drawStartX < 0 || s.drawStartX >= screenWidth) return;
        var currSlices = screenSlices.get(s.drawStartX);
        currSlices.add(s);
    }
}



