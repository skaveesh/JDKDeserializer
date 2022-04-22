# JdkSerializationRedisSerializer - Deserializer

This code is written to address an issue when the spring-data-redis key/value serializer is not defined and it uses the JdkSerializationRedisSerializer as default. When this happens key/value written to Redis will be garbled. When the code that is writing to the Redis is out of our control, we can use this code to retrieve such keys/values in original state,.

## Example

What we want, Redis template to write to Redis:

    key: 125d56b3-bcc4-44c6-8a27-0d53f7301b3e
    value: JVxPp3

What it actually writes:

    key: \xac\xed\x00\x05t\x00$125d56b3-bcc4-44c6-8a27-0d53f7301b3e
    value: \xac\xed\x00\x05t\x00\x06JVxPp3

## What these garbled values represents

Keys in Redis can only be Strings, but Spring lets you store Java objects as well. By default, Spring will convert the java object using JDK serializer. The JDK serializer adds those `\xac\xed...` bytes.

The first two bytes  `\xac\xed`  (hex: 0xACED) is the  `STREAM_MAGIC`  constant.

The next two bytes  `\x00\x05`  (hex: 0x0005) is the  `STREAM_VERSION`, version of the serialization protocol.

The next byte,  `t`  is 0x74 =  `TC_STRING`  meaning is a string object.

Finally,  `\x00\x06`  is the length of the string.

This protocol is described in the Object Serialization Stream Protocol, in  [6.4.2 Terminal Symbols and Constants](https://docs.oracle.com/javase/1.5.0/docs/guide/serialization/spec/protocol.html#10152)

source: [stackoverflow](https://stackoverflow.com/a/63477831/5410830)

## How to avoid it

You can use another serializer instead of leaving the key/value serializers empty or setting it to JdkSerializationRedisSerializer in your Spring application.

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();  
    redisTemplate.setConnectionFactory(connectionFactory);  
    redisTemplate.setKeySerializer(new StringRedisSerializer());  
    redisTemplate.setValueSerializer(new StringRedisSerializer());
