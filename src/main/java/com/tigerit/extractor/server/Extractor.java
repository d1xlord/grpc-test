package com.tigerit.extractor.server;

import com.google.protobuf.ByteString;
import com.tigerit.extractor.server.worker.ExtractedFinger;
import com.tigerit.extractor.server.worker.ExtractorServiceGrpc;
import com.tigerit.extractor.server.worker.ExtractorServiceGrpc.ExtractorServiceBlockingStub;
import com.tigerit.extractor.server.worker.InputFinger;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

/**
 * @author Nafis Ahmed
 */
public class Extractor {

    private final ManagedChannel channel;
    private final ExtractorServiceBlockingStub blockingStub;

    public Extractor(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    public Extractor(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ExtractorServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.MILLISECONDS);
    }

    public void GetExtractedFinger(String str) {

        InputFinger inputFinger = InputFinger.newBuilder().setFormat(2).setData(ByteString.copyFromUtf8(str)).build();
        ExtractedFinger extractedFinger;

        try {
            extractedFinger = blockingStub.extract(inputFinger);
            System.out.println("Got from GRPC:");
            System.out.println(extractedFinger.getQuality());
            System.out.println(extractedFinger.getIndex().toStringUtf8());
            System.out.println(extractedFinger.getData().toStringUtf8());
            System.out.println("////");
        } catch (StatusRuntimeException e) {
            System.out.println("RPC failed!!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Extractor extractor = new Extractor("localhost", 50551);
        try {
            extractor.GetExtractedFinger("Nafis Ahmed");
        } finally {
            extractor.shutdown();
        }
    }
}
