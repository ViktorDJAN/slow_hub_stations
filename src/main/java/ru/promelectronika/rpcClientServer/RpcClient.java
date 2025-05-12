package ru.promelectronika.rpcClientServer;


import lombok.Getter;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.controllerDetails.Controller;
import ru.promelectronika.enums.MessageEnumType;
import ru.promelectronika.logHandler.LogHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Getter
public class RpcClient implements Controller {
    private final Socket socket;
    public static final RpcClient NULL_RPC_CLIENT = new RpcClient(0);
    private int msgId;
    private List<Integer> waitingAnswer = new ArrayList<>();
    private DataOutputStream outputStream = null;
    private DataInputStream inputStream = null;
    private final byte[] buffer = new byte[2048];
    @Getter
    private final int connectorId;


    public RpcClient(int connectorId) {
        this.connectorId = connectorId;
        this.socket = new Socket();
        this.msgId = 0;
    }

    public void connectToSocket(String address, int port) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        this.socket.connect(socketAddress, 500);
        this.socket.setSoTimeout(500);
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
        ColorTuner.printPurpleText("** RPC_CLIENT ** CREATED ON ADDRESS: " + socket.getRemoteSocketAddress());
    }

//    public static RpcClient buildRpcClient(String address, int port) {
//        RpcClient client = new RpcClient();
//        try {
//            client.connectToSocket(address, port);
//            return client;
//        } catch (IOException e) {
//            System.out.println("NULL_RPC_CLIENT was created because of: " + e.getMessage());
//            return NULL_RPC_CLIENT;
//        }
//
//    }

    private byte[] read() {
        try {
            int len = inputStream.read(buffer);
            return Arrays.copyOf(buffer, len);
        } catch (IOException e) {
//            return new byte[] {};
            throw new RuntimeException(e);
        }
    }

    private void write(byte[] writeBuf) {
        try {
            outputStream.write(writeBuf);
            outputStream.flush();
        } catch (IOException e) {
            LogHandler.logThrowableMain(e);
        }
    }

    public void sendMessage(String nameMethod, Object[] parameters) throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packArrayHeader(4)
                    .packInt(MessageEnumType.REQUEST)
                    .packInt(msgId)
                    .packString(nameMethod)
                    .packArrayHeader(parameters.length);
            for (Object obj : parameters) {
                if (obj == null) {
                    packer.packNil();
                } else if (Long.class.equals(obj.getClass())) {
                    packer.packLong((Long) obj);
                } else if (Integer.class.equals(obj.getClass())) {
                    packer.packInt((Integer) obj);
                } else if (Short.class.equals(obj.getClass())) {
                    packer.packShort((Short) obj);
                } else if (Byte.class.equals(obj.getClass())) {
                    packer.packByte((Byte) obj);
                } else if (Double.class.equals(obj.getClass())) {
                    packer.packDouble((Double) obj);
                } else if (Float.class.equals(obj.getClass())) {
                    packer.packFloat((Float) obj);
                } else if (String.class.equals(obj.getClass())) {
                    packer.packString((String) obj);
                } else if (Boolean.class.equals(obj.getClass())) {
                    packer.packBoolean((Boolean) obj);
                }
            }
            packer.flush();
            this.write(packer.toByteArray());
        }
        waitingAnswer.add(msgId);
        msgId++;
        readMessage();
    }

    public void sendMessage(String nameMethod) throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packArrayHeader(4)
                    .packInt(MessageEnumType.REQUEST)
                    .packInt(msgId)
                    .packString(nameMethod)
                    .packArrayHeader(0);
            packer.flush();
            this.write(packer.toByteArray());
        }
        waitingAnswer.add(msgId);
        msgId++;
        readMessage();
    }

    public Object[] arrayHandler(ArrayValue value) {
        Object[] arrayObj = new Object[value.size()];
        for (int j = 0; j < value.size(); j++) {
            switch (value.get(j).getValueType()) {
                case NIL:
                    arrayObj[j] = null;
                    break;
                case BOOLEAN:
                    boolean b = value.get(j).asBooleanValue().getBoolean();
                    arrayObj[j] = b;
                    break;
                case INTEGER:
                    IntegerValue iv = value.get(j).asIntegerValue();
                    if (iv.isInIntRange()) {
                        int i = iv.toInt();
                        arrayObj[j] = i;
                    } else if (iv.isInLongRange()) {
                        long l = iv.toLong();
                        arrayObj[j] = l;
                    } else {
                        BigInteger i = iv.toBigInteger();
                        arrayObj[j] = i;
                    }
                    break;
                case FLOAT:
                    FloatValue fv = value.get(j).asFloatValue();
                    float f = fv.toFloat();   // use as float
                    double d = fv.toDouble(); // use as double
                    arrayObj[j] = d;
                    break;
                case STRING:
                    String s = value.get(j).asStringValue().asString();
                    arrayObj[j] = s;
                    break;

                case ARRAY:
                    ArrayValue a = value.get(j).asArrayValue();
                    arrayObj[j] = arrayHandler(a);
                    break;
            }
        }
        return arrayObj;
    }

    public void readMessage() throws IOException {
        byte[] msg = this.read();
        Object[] arrayObject;
        try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg)) {
            arrayObject = new Object[4];
            while (unpacker.hasNext()) {
                Value v = unpacker.unpackValue();
                if (v.getValueType() == ValueType.ARRAY) {
                    ArrayValue vArray = v.asArrayValue();
                    arrayObject = arrayHandler(vArray);
                }
            }
        }
        for (int index = 0; index < waitingAnswer.size(); index++) {
            if (waitingAnswer.get(index).equals(arrayObject[1])) {
                waitingAnswer.remove(index);
                break;
            }
        }
    }

    public void closeClientSocket() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void rpcConnectRequest(String remoteAddress, int remotePort) {}

    @Override
    public void rpcPing() {}

    @Override
    public void rpcAuthorize() {}

    @Override
    public void rpcUserStop() {}

    @Override
    public String getName() {
        return null;
    }
}
