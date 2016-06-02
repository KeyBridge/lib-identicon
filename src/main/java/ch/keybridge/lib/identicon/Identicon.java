/*
 * Copyright 2016 Key Bridge LLC.
 *
 * All rights reserved. Use is subject to license terms.
 * This software is protected by copyright.
 *
 * See the License for specific language governing permissions and
 * limitations under the License.
 */
package ch.keybridge.lib.identicon;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Utility class to simplify interaction with the Identicon image rendering
 * engine.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 created 06/01/16
 */
public class Identicon {

  /**
   * The default image size in pixels square.
   */
  private static final int DEFAULT_IDENTICON_SIZE = 64;
  /**
   * The version of this implementation.
   */
  private static final String VERSION = "1";

  /**
   * Generate a Identicon image based upon the provided Object instance.
   * <p>
   * The object instance is hashed and a unique image is generated based upon
   * the hash value.
   *
   * @param object    the object to hash (is null safe)
   * @param imageSize the image horizontal and vertical size in pixels (default
   *                  is 64)
   * @return a rendered Identicon image
   */
  public static BufferedImage generate(Object object, int imageSize) {
    IdenticonRenderer rendered = NineBlockIdenticonRenderer.getInstance();
    return rendered.render(Objects.hash(object), imageSize);
  }

  /**
   * Generate a Identicon image based upon the provided Object instance.
   * <p>
   * The object instance is hashed and a unique image is generated based upon
   * the hash value. The default image size of 64 pixels square is created.
   *
   *
   * @param object the object to hash
   * @return a rendered Identicon image (64 x 64 pixels)
   */
  public static BufferedImage generate(Object object) {
    return generate(object, DEFAULT_IDENTICON_SIZE);
  }

  /**
   * Generate a ETag to correspond with the generated Identicon image based upon
   * the provided Object instance.
   *
   * @param object    the object to hash
   * @param imageSize the image horizontal and vertical size in pixels (default
   *                  is 64)
   * @return the Identicon image ETAG string
   */
  public static String getETag(Object object, int imageSize) {
    StringBuilder s = new StringBuilder("W/\"");
    s.append(Integer.toHexString(Objects.hash(object)));
    s.append('@');
    s.append(imageSize);
    s.append('v');
    s.append(VERSION);
    s.append('\"');
    return s.toString();
  }

  /**
   * Generate a ETag to correspond with the generated Identicon image based upon
   * the provided Object instance.
   *
   * @param object the object to hash
   * @return the Identicon image ETAG string
   */
  public static String getETag(Object object) {
    return getETag(object, DEFAULT_IDENTICON_SIZE);
  }

}
