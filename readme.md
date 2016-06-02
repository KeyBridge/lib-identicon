# Identicon Image Generation Library

An Identicon is a visual representation of a hash value, usually of an IP address,
that serves to identify a user of a computer system as a form of avatar while protecting the users' privacy.

The original was (and this implementation is) a 9-block graphic.

## Example

![example](docs/identicon-eg.png)

## License

MIT

## Version

v1.0.0 - first release, based upon Don Park's original code ca 2012

## How to Use

This implementation has been generalized to accept and generate
a Identicon image based upon the hashed value of any Java object.

Object hashing is automatic and implemented using the `Objects.hash()`
utility method.

A Identicon image is most easily generated using the `Identicon` utility class:

    int imageSize = 128;
    String object = "Hello World";
    RenderedImage image = Identicon.generate(object, imageSize);

Image ETags are also available:

    String etag = Identicon.getETag(object, imageSize);

See the `IdenticonUnitTest` for more examples.

Enjoy!