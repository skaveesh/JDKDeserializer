package sh.skavee;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static sh.skavee.PropertyFileReader.getPropertyPath;
import static sh.skavee.PropertyFileReader.propertyReader;

public class JDKDeserializer {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JedisCluster jc = null;

        try {
            Properties redisProperties = propertyReader(getPropertyPath("redis"));
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            jedisClusterNodes.add(new HostAndPort(redisProperties.getProperty("redis_url"), Integer.parseInt(redisProperties.getProperty("port"))));
            jc = new JedisCluster(jedisClusterNodes);

            String value = (String) deserialize(jc.get(serialize("125d56b3-bcc4-44c6-8a27-0d53f7301b3e"))); // will receive: JVxPp3
            System.out.println(value);

        } finally {
            if(jc != null) {
                jc.close();
            }
        }


    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            return null;
        }
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }

    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);

        try {
            out.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            bos.close();
        }

        return bos.toByteArray();
    }

}
