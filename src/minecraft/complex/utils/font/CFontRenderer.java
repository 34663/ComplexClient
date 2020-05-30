package complex.utils.font;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import complex.utils.render.Colors;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFontRenderer extends CFont {
    protected CharData[] boldChars = new CharData['§'];
    protected CharData[] italicChars = new CharData['§'];
    protected CharData[] boldItalicChars = new CharData['§'];

    private final int[] colorCode = new int[32];
    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setupMinecraftColorcodes();
        setupBoldItalicIDs();
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = drawString(text, x + 0.5D, y + 0.5D, color, true);
        return Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawString(String text, double x, double y, int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, double x, double y, int color) {
        return drawString(text, x - getStringWidth(text) / 2, y, color);
    }

    public void drawTotalCenteredStringWithShadow(String text, double x, double y, int color) {
        drawStringWithShadow(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 2F, color);
    }

    public float drawCenteredStringWithShadow(String text, double x, int y, int color) {
        return drawStringWithShadow(text, x - getStringWidth(text) / 2, y, color);
    }

    public void drawTotalCenteredString(String text, double x, double y, int color) {
        drawString(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 2, color);
    }

    public void drawBorderedString(String text, float x, float y, int color) {
        GlStateManager.pushMatrix();
        GL11.glTranslated(0.5D, 0.0D, 0.0D);
        this.drawString(text, x, y, Colors.getColor(0, color >> 24 & 255), false);
        GL11.glTranslated(1.0D, 0.0D, 0.0D);
        this.drawString(text, x, y, Colors.getColor(0, color >> 24 & 255), false);
        GL11.glTranslated(-0.5D, 0.5D, 0.0D);
        this.drawString(text, x, y, Colors.getColor(0, color >> 24 & 255), false);
        GL11.glTranslated(0.0D, -1.0D, 0.0D);
        this.drawString(text, x, y, Colors.getColor(0, color >> 24 & 255), false);
        GL11.glTranslated(0.0D, 0.5D, 0.0D);
        this.drawString(text, x, y, color, false);
        GL11.glTranslated(-1.0D, -1.0D, 0.0D);
        GlStateManager.popMatrix();
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {

        x -= 1.0D;
        if (text == null)
            return 0.0F;
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }

        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        CharData[] currentData = this.charData;
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        boolean randomCase = false;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean render = true;
        x *= 2.0D;
        y = (y - 3.0D) * 2.0D;

        GL11.glPushMatrix();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
        int size = text.length();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        GL11.glBindTexture(3553, this.tex.getGlTextureId());
        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if ((character == '§') && (i < size)) {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());

                    currentData = this.charData;
                    if ((colorIndex < 0) || (colorIndex > 15))
                        colorIndex = 15;
                    if (shadow)
                        colorIndex += 16;
                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((colorcode >> 16 & 0xFF) / 255.0F, (colorcode >> 8 & 0xFF) / 255.0F, (colorcode & 0xFF) / 255.0F, alpha);
                } else if (colorIndex == 16) {
                    randomCase = true;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());

                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texBold.getGlTextureId());

                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());

                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texItalic.getGlTextureId());

                        currentData = this.italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
                    GlStateManager.bindTexture(this.tex.getGlTextureId());

                    currentData = this.charData;
                }
                i++;
            } else if ((character < currentData.length) && (character >= 0)) {
                GL11.glBegin(4);
                drawChar(currentData, character, (float) x, (float) y);
                GL11.glEnd();
                if (strikethrough)
                    drawLine(x, y + currentData[character].height / 2, x + currentData[character].width - 8.0D, y + currentData[character].height / 2, 1.0F);
                if (underline)
                    drawLine(x, y + currentData[character].height - 2.0D, x + currentData[character].width - 8.0D, y + currentData[character].height - 2.0D, 1.0F);
                x += currentData[character].width - 8 + this.charOffset;
            }
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();

        return (float) x * 2.0F;
    }

    public int getStringWidth(String text) {
        if (text == null)
            return 0;
        int width = 0;
        CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if ((character == '§') && (i < size)) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentData = this.boldItalicChars;
                    } else
                        currentData = this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        currentData = this.boldItalicChars;
                    } else
                        currentData = this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                i++;
            } else if ((character < currentData.length) && (character >= 0)) {
                width += currentData[character].width - 8 + this.charOffset;
            }
        }

        return width / 2;
    }

    public void setFont(Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }

    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
    }

    private void drawLine(double x, double y, double x1, double y1, float width) {
        GL11.glDisable(3553);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public List<String> wrapWords(String text, double width) {
        List finalWords = new ArrayList();
        if (getStringWidth(text) > width) {
            String[] words = text.split(" ");
            String currentWord = "";
            char lastColorCode = 65535;
            String[] arrayOfString1;
            int j = (arrayOfString1 = words).length;
            for (int i = 0; i < j; i++) {
                String word = arrayOfString1[i];
                for (int ii = 0; i < word.toCharArray().length; i++) {
                    char c = word.toCharArray()[i];

                    if ((c == '§') && (i < word.toCharArray().length - 1)) {
                        lastColorCode = word.toCharArray()[(i + 1)];
                    }
                }
                if (getStringWidth(currentWord + word + " ") < width) {
                    currentWord = currentWord + word + " ";
                } else {
                    finalWords.add(currentWord);
                    currentWord = '§' + lastColorCode + word + " ";
                }
            }
            if (currentWord.length() > 0)
                if (getStringWidth(currentWord) < width) {
                    finalWords.add('§' + lastColorCode + currentWord + " ");
                    currentWord = "";
                } else {
                    for (String s : formatString(currentWord, width))
                        finalWords.add(s);
                }
        } else {
            finalWords.add(text);
        }

        return finalWords;
    }

    public List<String> formatString(String string, double width) {
        List finalWords = new ArrayList();
        String currentWord = "";
        char lastColorCode = 65535;
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if ((c == '§') && (i < chars.length - 1)) {
                lastColorCode = chars[(i + 1)];
            }

            if (getStringWidth(currentWord + c) < width) {
                currentWord = currentWord + c;
            } else {
                finalWords.add(currentWord);
                currentWord = '§' + lastColorCode + String.valueOf(c);
            }
        }

        if (currentWord.length() > 0) {
            finalWords.add(currentWord);
        }

        return finalWords;
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }
}