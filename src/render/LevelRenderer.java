package render;

import entities.Door;
import entities.Player;
import graphics.Texture;
import interfaces.ILightSource;
import interfaces.IRenderable;
import level.Level;
import level.Tile;
import utils.math.*;
import utils.render.RenderContext;
import utils.render.TextureManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;


public class LevelRenderer {

    private Screen screen;
    private Level level;
    private double[] depthBuffer;
    private Vector2d[] pixWorldPos;

    private double[] transDepthBuffer;
    private Vector2d[] transPixWorldPos;

    private Camera camera;
    private Graphics2D g2d;
    private MapIntersectionFinder interFinder;

    private int screenWidth;
    private int screenHeight;
    private int[] screenPixels;
    private int[] transScreenPixels;

    private static double wallHeight = 1.0; //in screen coords - wall has height of the screen height
    private List<List<Intersection>> intersections;

    public LevelRenderer(Level level, Screen screen, Graphics2D g2d, Camera camera) {
        this.screen = screen;
        this.level = level;
        this.camera = camera;
        this.g2d = screen.getGraphics();

        this.screenHeight = screen.getHeight();
        this.screenWidth = screen.getWidth();
        this.screenPixels = screen.getPixels();
        this.depthBuffer = new double[screenWidth * screenHeight];
        this.transDepthBuffer = new double[screenWidth * screenHeight];
        this.transScreenPixels = new int[screenWidth * screenHeight];


        this.pixWorldPos = new Vector2d[screenWidth * screenHeight];
        this.transPixWorldPos = new Vector2d[screenWidth * screenHeight];
        for(int i = 0; i < this.pixWorldPos.length; i++) {
            this.pixWorldPos[i] = new Vector2d();
            this.transPixWorldPos[i] = new Vector2d();
        }

        this.intersections = new ArrayList<>(this.screenWidth);
        for(int i = 0; i < screenWidth; i++) this.intersections.add(i,new LinkedList<>());

        this.interFinder = new MapIntersectionFinder(level);
    }

    public void renderLevel(double dt) {
        var lightSources = level.getLightSources();

        Arrays.fill(depthBuffer, Double.MAX_VALUE);
        Arrays.fill(transDepthBuffer, Double.MAX_VALUE);
        Arrays.fill(transScreenPixels, 0x00);
        renderBackground();

        for(int col = 0; col < screenWidth; col++) {
            double cameraX = 2.0 * (double)(col) / (double)(screenWidth) - 1; //x-coordinate in camera space
            Ray ray = MathUtils.constructRay(this.camera, cameraX);
            var currColInterList = intersections.get(col);
            currColInterList.clear();

            interFinder.findAllIntersections(ray, this.camera.getAngle(), currColInterList);

            //from farthest column start drawing floor and ceiling
            var inter = currColInterList.get(currColInterList.size() - 1);
            Tile t = level.getMapInfo().getTileAt((int)inter.position.x, (int)inter.position.y);
            double projectedHeight = Math.round(((screenHeight * t.height) / inter.distToWall));

            int endY = (int)(screenHeight / 2 + (projectedHeight / 2));
            if(endY >= screenHeight) endY = screenHeight - 1;

            drawFloorAndCeiling(col, ray, endY, lightSources);

            for (int i = currColInterList.size() - 1; i >= 0; i--) {
                inter = currColInterList.get(i);
                t = level.getMapInfo().getTileAt((int)inter.position.x, (int)inter.position.y);
                projectedHeight = Math.round(((screenHeight * t.height) / inter.distToWall));

                int startY = (int)(screenHeight / 2 - (projectedHeight / 2));
                endY = (int)(screenHeight / 2 + (projectedHeight / 2));
                if(startY < 0) startY = 0;
                if(endY >= screenHeight) endY = screenHeight - 1;

                renderWallStripe(col, ray, inter, projectedHeight, startY, endY, lightSources);
            }
        }

        applyLights();
        renderEntities(screenWidth, screenHeight, screenPixels, lightSources);
        mergeAlpha();

        renderPlayer(level.getPlayer());
        renderGUI(level.getPlayer());
    }

    public void mergeAlpha() {
        for(int i = 0; i < screenPixels.length; i++) {
            //transparent is in foreground
            if(transDepthBuffer[i] < depthBuffer[i]) {
                int transPixBlended = MathUtils.blendPixel(transScreenPixels[i], screenPixels[i]);
                screenPixels[i] = transPixBlended;
            }
        }
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

//            if(distance <= depthBuffer[cPos]) {
                pixWorldPos[cPos].setXY(mapPositionX, mapPositionY);
                depthBuffer[cPos] = distance;
                screenPixels[cPos] = MathUtils.blendPixel(cColor, screenPixels[cPos]);
//            }

//            if(distance <= depthBuffer[fPos]) {
                pixWorldPos[fPos].setXY(mapPositionX, mapPositionY);
                depthBuffer[fPos] = distance;
                screenPixels[fPos] = MathUtils.blendPixel(fColor, screenPixels[fPos]);
//            }
        }

    }

    private void renderEntities(int screenWidth, int screenHeight, int[] screenPixels, List<ILightSource> lightSrcs) {
        List<IRenderable> entities = level.getRenderables();
        entities.sort(Comparator.comparingDouble((IRenderable r) -> MathUtils.getSimpleDistance(r.getRenderContext().getPosition(), camera.position)).reversed());
        entities.forEach((entity) -> drawSprite(screenWidth, screenHeight, screenPixels, entity, lightSrcs));
    }

    private void renderColorStripe(int screenWidth, int[] screenPixels, int col, int startY) {
        for (int y = 0; y < startY; y++) {
            screenPixels[col + y * screenWidth] = Color.BLACK.getRGB();
        }
    }

    private void renderWallStripe(int col, Ray ray, Intersection inter, double projectedHeight, int startY, int endY, List<ILightSource> lightSources) {
        int currColor;//TODO: maybe renderStrategy for every IRenderable => would eliminate ifs

        Texture tex = inter.t.wallTexture;
        double step = tex.getHeight() / projectedHeight;
        double texPos = (startY - (double)screenHeight / 2 + projectedHeight / 2) * step;

        for(int y = startY; y < endY; y++) {
            double wallX = inter.isVertical ? inter.position.y : inter.position.x;
            if(inter.isVertical) {
                wallX = wallX - Math.floor(inter.position.y);
            } else {
                if(inter.wallType == Tile.HORIZ_DOOR) {
                    if(!ray.isFacingUp()) {
                        wallX -= Math.floor(inter.position.x);
                        wallX = 1.0 - wallX - level.getDoor((int)inter.position.x, (int)inter.position.y).getOffset();
                    } else {
                        Door d = level.getDoor((int)inter.position.x, (int)inter.position.y);
                        wallX -= Math.floor(inter.position.x) - d.getOffset();
                    }
                }
                else {
                    wallX -= Math.floor(inter.position.x);
                }
            }

            int texelX = (int) (wallX * (tex.getWidth()));

            if((inter.isVertical && !ray.isFacingRight()) || (!inter.isVertical && !ray.isFacingUp())) {
                texelX = tex.getWidth() - texelX - 1;
            }

            currColor = tex.getPixelAt(texelX, (int)texPos);
            texPos += step;

            int screenPos = col + y * screenWidth;
            int pix = screenPixels[screenPos];
            int alpha = ((currColor >> 24) & 0xff);
            //we would have to blend, render to another buffer
            if(alpha == 255) {
                this.depthBuffer[screenPos] = inter.distToWall;
                this.pixWorldPos[screenPos].setXY(inter.position.x, inter.position.y);
                this.screenPixels[screenPos] = MathUtils.blendPixel(currColor, pix);
            } else if(alpha != 0){
                this.transDepthBuffer[screenPos] = inter.distToWall;
                this.transPixWorldPos[screenPos].setXY(inter.position.x, inter.position.y);
                int lighted = applyLightsToPixel(this.transPixWorldPos[screenPos], currColor, lightSources);
                this.transScreenPixels[screenPos] = lighted;
            }
        }
    }

    private void applyLights() {

        for(int i = 0; i < screenPixels.length; i++) {
            int finalPixel = 0x00;
            int pixel = screenPixels[i];

            for(ILightSource lightSrc: level.getLightSources()) {
                var props = lightSrc.getLightSourceProperty();
                double distLightToInter = MathUtils.getSimpleDistance(pixWorldPos[i], props.worldPosition);
                double attentuation = 1.0 / (1.0 + props.attentuation * Math.pow(distLightToInter, 2));
                int currR = ((pixel & 0x00ff0000) >> 16);
                int currG = ((pixel & 0x0000ff00) >> 8);
                int currB = (pixel & 0x000000ff);
                //ARGB
                int red = ((int)(currR * attentuation * props.red));
                int green = ((int)(currG * attentuation * props.green));
                int blue = ((int)(currB * attentuation * props.blue));
                finalPixel += ((red << 16) | (green << 8) | blue);
                finalPixel = MathUtils.clamp(finalPixel, 0x00ffffff, 0x00000000);
                screenPixels[i] = (pixel & 0xff000000) | finalPixel;
            }
        }

    }

    private int applyLightsToPixel(Vector2d pixelWorldPos, int pixel, List<ILightSource> lightSources) {
        int finalPixel = 0x00;

        for (ILightSource lightSrc: lightSources) {
            var props = lightSrc.getLightSourceProperty();
            double distLightToInter = MathUtils.getSimpleDistance(pixelWorldPos, props.worldPosition);
            double attentuation = 1.0 / (1.0 + props.attentuation * Math.pow(distLightToInter, 2));
            int currR = ((pixel & 0x00ff0000) >> 16);
            int currG = ((pixel & 0x0000ff00) >> 8);
            int currB = (pixel & 0x000000ff);
            //ARGB
            int red = ((int)(currR * attentuation * props.red));
            int green = ((int)(currG * attentuation * props.green));
            int blue = ((int)(currB * attentuation * props.blue));
            finalPixel += ((red << 16) | (green << 8) | blue);
            finalPixel = MathUtils.clamp(finalPixel, 0x00ffffff, 0x00000000);
        }

        return (pixel & 0xff000000) | finalPixel;
    }

    private int applyLightsToPixelAndBlend(Vector2d pixelWorldPos, int pixel, int oldPix, List<ILightSource> lightSources) {
        int lightedPix = applyLightsToPixel(pixelWorldPos, pixel, lightSources);
        return MathUtils.blendPixel(lightedPix, oldPix);
    }


    private void drawSprite(int screenWidth, int screenHeight, int[] screenPixels, IRenderable entity, List<ILightSource> lightSrcs) {
        RenderContext context = entity.getRenderContext();
        Vector2d relativePos = MathUtils.getRelativePos(context.getPosition(), camera.position);

        //transform sprite with the inverse camera matrix
        double invDet = 1.0f / (camera.plane.x * camera.direction.y - camera.direction.x * camera.plane.y); //required for correct matrix multiplication
        double transformX = invDet * (camera.direction.y * relativePos.x - camera.direction.x * relativePos.y);
        double transformY = invDet * (- camera.plane.y * relativePos.x + camera.plane.x * relativePos.y); //this is actually the depth inside the screen, that what Z is in 3D

        int spriteScreenX = (int)((screenWidth / 2) * (1 + transformX / transformY));

        int spriteHeight = Math.abs((int)(screenHeight / (transformY))); //using 'transformY' instead of the real distance prevents fisheye

        int drawStartY = -spriteHeight / 2 + screenHeight / 2;
        if(drawStartY < 0) drawStartY = 0;

        int drawEndY = spriteHeight / 2 + screenHeight / 2;
        if(drawEndY >= screenHeight) drawEndY = screenHeight - 1;

        int spriteWidth = Math.abs((int)(screenHeight / transformY));

        int drawStartX = -spriteWidth / 2 + spriteScreenX;
        if(drawStartX < 0) drawStartX = 0;

        int drawEndX = spriteWidth / 2 + spriteScreenX;
        if(drawEndX >= screenWidth) drawEndX = screenWidth - 1;

        double angle = MathUtils.getAngle(camera.position, entity.getRenderContext().getPosition());

        for(int stripe = drawStartX; stripe < drawEndX; stripe++)
        {
            int texX = (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * 64 / spriteWidth) / 256;
            if(transformY > 0 && stripe > 0 && stripe < screenWidth) {
                for(int y = drawStartY; y < drawEndY; y++) //for every pixel of the current stripe
                {
                    int screenPos = stripe + y * screenWidth;
                    if(transformY < depthBuffer[screenPos]) {
                        int d = (y) * 256 - screenHeight * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
                        int texY = ((d * context.getTexture().getWidth()) / spriteHeight) / 256;
                        int pix = context.getTexture(angle).getPixelAt(texX, texY);

                        //dont draw pixels (do not have to compute stuff) that should be transparent - works only for ARGB format
                        //TODO: check if it is bottleneck without if
                        int alpha = (pix >> 24) & 0xff;
                        if(alpha != 0) {
                            depthBuffer[screenPos] = transformY;
                            pixWorldPos[screenPos].setXY(context.getPosition());
                            int oldPix = this.screenPixels[screenPos]; //we have background here (walls etc)
                            screenPixels[stripe + y * screenWidth] = applyLightsToPixelAndBlend(context.getPosition(), pix, oldPix, lightSrcs);
                        }
                    }
                }
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
}
