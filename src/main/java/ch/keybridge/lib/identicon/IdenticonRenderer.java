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

import java.awt.image.BufferedImage;

/**
 * Identicon renderer interface.
 * <p>
 * An Identicon is a visual representation of a hash value, usually of an IP
 * address, that serves to identify a user of a computer system as a form of
 * avatar while protecting the users' privacy.
 *
 * @author Don Park donpark@docuverse.com
 */
public interface IdenticonRenderer {

  /**
   * Render the numeric hashcode into a unique Identicon image.
   *
   * @param hashCode  the numeric hash code to render.
   * @param imageSize the horizontal and vertical image size to create
   * @return the rendered Identicon image
   */
  public BufferedImage render(Integer hashCode, int imageSize);
}
