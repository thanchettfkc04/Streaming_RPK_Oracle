import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

public class DynamicTopicRouter {
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();

        // Subscribe vào tất cả topics kết thúc bằng "_in"
        builder.stream(Pattern.compile("topic_.*_in"), Consumed.with(Serdes.String(), Serdes.String()))
            .process(() -> new DynamicTopicProcessor());

        new KafkaStreams(builder.build(), config).start();
    }
}

class DynamicTopicProcessor implements ProcessorSupplier<String, String> {
    @Override
    public Processor<String, String> get() {
        return new Processor<>() {
            private ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public void process(String key, String value) {
                // 1. Trích xuất abcd từ tên topic đầu vào
                String inputTopic = context.topic();
                String abcd = inputTopic.replaceAll("topic_(.*)_in", "$1");
                
                // 2. Kiểm tra quyền qua Redis
                if (!checkPermission(key, abcd)) {
                    throw new SecurityException("Access denied");
                }
                
                // 3. Truy vấn Oracle và xử lý dữ liệu
                String result = queryOracle(value);
                
                // 4. Ghi vào topic_out tương ứng
                String outputTopic = "topic_" + abcd + "_out";
                context.forward(key, result);
                context.commit();
            }
        };
    }
}