//package top.kwseeker.zk.configcenter.core.utils;
//
//import org.I0Itec.zkclient.exception.ZkMarshallingError;
//import org.I0Itec.zkclient.serialize.ZkSerializer;
//
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//
//public class StringZkSerializer implements ZkSerializer {
//
//    final Charset charset = StandardCharsets.UTF_8;
//
//    @Override
//    public byte[] serialize(Object data) throws ZkMarshallingError {
//        return data.toString().getBytes(this.charset);
//    }
//
//    @Override
//    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
//        return new String(bytes, this.charset);
//    }
//}
