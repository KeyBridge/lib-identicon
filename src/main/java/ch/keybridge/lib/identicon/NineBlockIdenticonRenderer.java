/*
 * The MIT License
 *
 * Copyright (c) 2007-2014 Don Park <donpark@docuverse.com>
 * Contributor   2014-2014 Paulo Miguel Almeida Rodenas <paulo.ubuntu@gmail.com>
 * Contributor & Copyright  2016 Key Bridge LLC.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ch.keybridge.lib.identicon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * 9-block Identicon visual hash renderer.
 * <p>
 * An Identicon is a visual representation of a hash value, usually of an IP
 * address, that serves to identify a user of a computer system as a form of
 * avatar while protecting the users' privacy.
 * <p>
 * This (original) Identicon is a 9-block graphic, and the representation has
 * been extended to other graphic forms by third parties.
 * <p>
 * This is refactored / renamed from the
 * <code>NineBlockIdenticonRenderer2</code> class file.
 * <p>
 * This implementation uses only the lower 32 bits of the input identicon code.
 *
 * @author Don Park donpark@docuverse.com
 * @author Jesse Caulfiled
 * @since 1.0.0 created 06/01/2016
 */
public class NineBlockIdenticonRenderer implements IdenticonRenderer {

  /*
   * Each patch is a polygon created from a list of vertices on a 5 by 5 grid.
   * Vertices are numbered from 0 to 24, starting from top-left corner of the
   * grid, moving left to right and top to bottom.
   */
  private static final int PATCH_GRIDS = 5;
  private static final double DEFAULT_PATCH_SIZE = 20.0;
  private static final byte PATCH_SYMMETRIC = 1;
  private static final byte PATCH_INVERTED = 2;
  private static final int PATCH_MOVETO = -1;
  private static final byte[] PATCH0 = {0, 4, 24, 20};
  private static final byte[] PATCH1 = {0, 4, 20};
  private static final byte[] PATCH2 = {2, 24, 20};
  private static final byte[] PATCH3 = {0, 2, 20, 22};
  private static final byte[] PATCH4 = {2, 14, 22, 10};
  private static final byte[] PATCH5 = {0, 14, 24, 22};
  private static final byte[] PATCH6 = {2, 24, 22, 13, 11, 22, 20};
  private static final byte[] PATCH7 = {0, 14, 22};
  private static final byte[] PATCH8 = {6, 8, 18, 16};
  private static final byte[] PATCH9 = {4, 20, 10, 12, 2};
  private static final byte[] PATCH10 = {0, 2, 12, 10};
  private static final byte[] PATCH11 = {10, 14, 22};
  private static final byte[] PATCH12 = {20, 12, 24};
  private static final byte[] PATCH13 = {10, 2, 12};
  private static final byte[] PATCH14 = {0, 2, 10};
  /**
   * A circular array of patches.
   */
  private static final byte[] PATCH_TYPES[] = {PATCH0, PATCH1, PATCH2, PATCH3, PATCH4,
                                               PATCH5, PATCH6, PATCH7, PATCH8, PATCH9,
                                               PATCH10, PATCH11, PATCH12, PATCH13, PATCH14,
                                               PATCH0};
  /**
   * Set of flags used to determine inversion status.
   */
  private static final byte PATCH_FLAGS[] = {PATCH_SYMMETRIC, 0, 0, 0,
                                             PATCH_SYMMETRIC, 0, 0, 0,
                                             PATCH_SYMMETRIC, 0, 0, 0, 0, 0, 0,
                                             PATCH_SYMMETRIC + PATCH_INVERTED};
  /**
   * Sequence to identify the center patch type.
   */
  private static final int CENTER_PATCH_TYPES[] = {0, 4, 8, 15};

  /**
   * The size in pixels at which each patch will be rendered before they are
   * scaled down to the requested output image size.
   */
  private double patchSize;
  /**
   * Array of java.awt patch shapes.
   */
  private GeneralPath[] patchShapes;
  /**
   * used to center patch shape at origin because shape rotation works
   * correctly.
   */
  private double patchOffset;
  /**
   * The background color. Default is white.
   */
  private Color backgroundColor = Color.WHITE;

  /**
   * Private constructor. Use {@code getInstance()}.
   */
  private NineBlockIdenticonRenderer() {
  }

  /**
   * Get a running instance of this Identicon image rendering engine.
   *
   * @return a ready instance.
   */
  public static IdenticonRenderer getInstance() {
    NineBlockIdenticonRenderer renderer = new NineBlockIdenticonRenderer();
    renderer.setPatchSize(DEFAULT_PATCH_SIZE);
    return renderer;
  }

  /**
   * Returns the size in pixels at which each patch will be rendered before they
   * are scaled down to requested identicon size.
   *
   * @return the patch size in pixels
   */
  public double getPatchSize() {
    return patchSize;
  }

  /**
   * Set the size in pixels at which each patch will be rendered before they are
   * scaled down to requested output image size.
   * <p>
   * Default size is 20 pixels which means, for a 9-block image a 60x60 image
   * will be rendered and scaled down.
   *
   * @param patchSize the patch size in pixels
   */
  public final void setPatchSize(double patchSize) {
    this.patchSize = patchSize;
    this.patchOffset = patchSize / 2.0; // used to center patch shape at origin.
    double patchScale = patchSize / 4.0;
    this.patchShapes = new GeneralPath[PATCH_TYPES.length];
    for (int i = 0; i < PATCH_TYPES.length; i++) {
      GeneralPath patch = new GeneralPath(GeneralPath.WIND_NON_ZERO);
      boolean moveTo = true;
      byte[] patchVertices = PATCH_TYPES[i];
      for (int j = 0; j < patchVertices.length; j++) {
        int v = (int) patchVertices[j];
        if (v == PATCH_MOVETO) {
          moveTo = true;
        }
        double vx = ((v % PATCH_GRIDS) * patchScale) - patchOffset;
        double vy = (Math.floor(((double) v) / PATCH_GRIDS)) * patchScale - patchOffset;
        if (!moveTo) {
          patch.lineTo(vx, vy);
        } else {
          moveTo = false;
          patch.moveTo(vx, vy);
        }
      }
      patch.closePath();
      this.patchShapes[i] = patch;
    }
  }

  /**
   * Get the background color.
   *
   * @return the background color.
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Set the background color.
   *
   * @param backgroundColor the background color.
   */
  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BufferedImage render(Integer code, int size) {
    return renderQuilt(code, size);
  }

  /**
   * Internal worker method to render the Identicon quilt.
   * <p>
   * Size of the returned identicon image is determined by patchSize set using
   * {@link #setPatchSize(double)}. Since a 9-block identicon consists of 3x3
   * patches the width and height will be 3 times the patch size.
   *
   * @param hashCode  the Identicon hash code to render
   * @param imageSize the horizontal and vertical image size to create
   * @return the rendered Identicon image
   */
  private BufferedImage renderQuilt(Integer hashCode, int imageSize) {
    /**
     * PREPARE
     * <pre>
     * decode the code into parts
     * bit 0-1: middle patch type
     * bit 2: middle invert
     * bit 3-6: corner patch type
     * bit 7: corner invert
     * bit 8-9: corner turns
     * bit 10-13: side patch type
     * bit 14: side invert
     * bit 15: corner turns
     * bit 16-20: blue color component
     * bit 21-26: green color component
     * bit 27-31: red color component
     * </pre>
     */
    int middleType = CENTER_PATCH_TYPES[hashCode & 0x3];
    boolean middleInvert = ((hashCode >> 2) & 0x1) != 0;
    int cornerType = (hashCode >> 3) & 0x0f;
    boolean cornerInvert = ((hashCode >> 7) & 0x1) != 0;
    int cornerTurn = (hashCode >> 8) & 0x3;
    int sideType = (hashCode >> 10) & 0x0f;
    boolean sideInvert = ((hashCode >> 14) & 0x1) != 0;
    int sideTurn = (hashCode >> 15) & 0x3;
    int blue = (hashCode >> 16) & 0x01f;
    int green = (hashCode >> 21) & 0x01f;
    int red = (hashCode >> 27) & 0x01f;
    /**
     * color components are used at top of the range for color difference. Use
     * white background for now.
     * <p>
     * TODO: support transparency.
     */
    Color fillColor = new Color(red << 3, green << 3, blue << 3);
    /**
     * Outline shapes with a noticeable color (complementary will do) if shape
     * color and background color are too similar (measured by color distance).
     */
    Color strokeColor = null;
    if (getColorDistance(fillColor, backgroundColor) < 32.0f) {
      strokeColor = getComplementaryColor(fillColor);
    }
    /**
     * RENDER
     * <p>
     * Prepare a square RGB buffered image.
     */
    BufferedImage targetImage = new BufferedImage(imageSize,
                                                  imageSize,
                                                  BufferedImage.TYPE_INT_RGB);
    Graphics2D g = targetImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);
    g.setBackground(backgroundColor);
    g.clearRect(0, 0, imageSize, imageSize);
    /**
     * Start drawing.
     */
    double blockSize = imageSize / 3.0f;
    double blockSize2 = blockSize * 2.0f;
    /**
     * middle patch
     */
    drawPatch(g, blockSize, blockSize, blockSize, middleType, 0, middleInvert, fillColor, strokeColor);
    /**
     * side patches, starting from top and moving clock-wise
     */
    drawPatch(g, blockSize, 0, blockSize, sideType, sideTurn++, sideInvert, fillColor, strokeColor);
    drawPatch(g, blockSize2, blockSize, blockSize, sideType, sideTurn++, sideInvert, fillColor, strokeColor);
    drawPatch(g, blockSize, blockSize2, blockSize, sideType, sideTurn++, sideInvert, fillColor, strokeColor);
    drawPatch(g, 0, blockSize, blockSize, sideType, sideTurn++, sideInvert, fillColor, strokeColor);
    /**
     * corner patches, starting from top left and moving clock-wise
     */
    drawPatch(g, 0, 0, blockSize, cornerType, cornerTurn++, cornerInvert, fillColor, strokeColor);
    drawPatch(g, blockSize2, 0, blockSize, cornerType, cornerTurn++, cornerInvert, fillColor, strokeColor);
    drawPatch(g, blockSize2, blockSize2, blockSize, cornerType, cornerTurn++, cornerInvert, fillColor, strokeColor);
    drawPatch(g, 0, blockSize2, blockSize, cornerType, cornerTurn++, cornerInvert, fillColor, strokeColor);
    /**
     * Done drawing.
     */
    g.dispose();

    return targetImage;
  }

  /**
   * Internal method to draw a patch.
   *
   * @param g           the graphics utility
   * @param x           the x start
   * @param y           the y start
   * @param size        the patch size
   * @param patch       the patch
   * @param turn        the turn factor
   * @param invert      corner invert factor
   * @param fillColor   the fill color
   * @param strokeColor the stroke color
   */
  private void drawPatch(Graphics2D g, double x, double y, double size, int patch, int turn, boolean invert, Color fillColor, Color strokeColor) {
    /**
     * Assert usable patch and turn values.
     */
    assert patch >= 0;
    assert turn >= 0;
    /**
     * Determine if we are inverted.
     */
    patch %= PATCH_TYPES.length;
    turn %= 4;
    if ((PATCH_FLAGS[patch] & PATCH_INVERTED) != 0) {
      invert = !invert;
    }

    Shape shape = patchShapes[patch];
    double scale = ((double) size) / ((double) patchSize);
    double offset = size / 2.0f;
    /**
     * paint background
     */
    g.setColor(invert ? fillColor : backgroundColor);
    g.fill(new Rectangle2D.Double(x, y, size, size));
    /**
     * transform the patch
     */
    AffineTransform savet = g.getTransform();
    g.translate(x + offset, y + offset);
    g.scale(scale, scale);
    g.rotate(Math.toRadians(turn * 90));
    /**
     * If stroke color was specified, apply stroke stroke color should be
     * specified if fore color is too close to the back color.
     */
    if (strokeColor != null) {
      g.setColor(strokeColor);
      g.draw(shape);
    }
    /**
     * render rotated patch using fore color (back color if inverted)
     */
    g.setColor(invert ? backgroundColor : fillColor);
    g.fill(shape);
    g.setTransform(savet);
  }

  /**
   * Calculate the distance between two colors.
   *
   * @param c1 the first color
   * @param c2 the second color
   * @return the distance
   */
  private double getColorDistance(Color c1, Color c2) {
    double dx = c1.getRed() - c2.getRed();
    double dy = c1.getGreen() - c2.getGreen();
    double dz = c1.getBlue() - c2.getBlue();
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * Calculate a complementary color.
   *
   * @param color the input color
   * @return a complementary color
   */
  private Color getComplementaryColor(Color color) {
    return new Color(color.getRGB() ^ 0x00FFFFFF);
  }
}
