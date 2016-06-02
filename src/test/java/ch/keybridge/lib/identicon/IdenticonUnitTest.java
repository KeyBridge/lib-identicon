/*
 * The MIT License
 *
 * Copyright 2016 Key Bridge LLC.
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

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.junit.Test;

/**
 *
 * @author jesse
 */
public class IdenticonUnitTest {

  private static final String INIT_PARAM_INET_SALT = "1321321321321321";
  private static final String PARAM_IDENTICON_CODE = "code";
  private static final int DEFAULT_IDENTICON_SIZE = 16;
  private int version = 1;
  private static final String IDENTICON_IMAGE_FORMAT = "PNG";

  private static final String IDENTICON_IMAGE_MIMETYPE = "image/png";

  private IdenticonRenderer renderer = NineBlockIdenticonRenderer.getInstance();

  public IdenticonUnitTest() {
  }

  @Test
  public void testRender() throws Exception {
    System.out.println("Identicon TestRender");
    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
    String inetSalt = INIT_PARAM_INET_SALT;
    String codeParam = PARAM_IDENTICON_CODE;

//    String remoteAddr = "192.168.1.1";
    int code = Objects.hash("keybridge@keybridgeglobal.com");
//    int code = IdenticonUtil.getIdenticonCode(InetAddress.getByName("remoteAddr"));
    System.out.println("code: " + code);

//    int size = IdenticonUtil.getIdenticonSize("48");
    int size = 256;
    System.out.println("size: " + size);
    String identiconETag = Identicon.getETag(code, size);
    System.out.println("etag: " + identiconETag);

//    byte[] imageBytes = null;
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    RenderedImage image = renderer.render(code, size);
    ImageIO.write(image, IDENTICON_IMAGE_FORMAT, byteOut);
//    imageBytes = byteOut.toByteArray();

    Path out = Paths.get("/tmp", "identicon");
    System.out.println("write to " + out);
    ImageIO.write(image, IDENTICON_IMAGE_FORMAT, out.toFile());

  }

  @Test
  public void testETag() {
    System.out.println("Identicon TestETag ");
    int code = Objects.hash("keybridge@keybridgeglobal.com");
//    int code = IdenticonUtil.getIdenticonCode(InetAddress.getByName("remoteAddr"));
    System.out.println("code: " + code);

    String etag = Identicon.getETag(code, 48);

//    W/"bdb5e3e5@48v1"
    System.out.println("etag " + etag);

  }

  @Test
  public void testUtil() throws IOException {
    System.out.println("Identicon TestUtility ");

    String email = "rendertest@keybridgeglobal.com";
    RenderedImage image = Identicon.generate(email, 128);
//    RenderedImage image = Identicon.generate(null, 128);

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ImageIO.write(image, IDENTICON_IMAGE_FORMAT, byteOut);
    Path out = Paths.get("/tmp", "identicon-util");
    System.out.println("write to " + out);
    ImageIO.write(image, IDENTICON_IMAGE_FORMAT, out.toFile());

  }
}
