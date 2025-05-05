package lld;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) throws IOException {
        String filePath = "/Users/rajat/Documents/workspace/personal/Success/MassiveSuccess/src/main/resources/manifest.txt";
        String appendText = "file_url|timestamp|feed|feed_version|md5|export_type";
        File file = new File(filePath);
        FileWriter fr = new FileWriter(file, true);
        fr.write(appendText);

        try (Stream<Path> paths = Files.walk(Paths.get("/Users/rajat/Documents/RingCentral/incident"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            fr.write("\n");
                            fr.write("s3://gdc-ms-cust/AIDAJSOEI47V3L7TKEAXC_gdc-ms-cust_ConnectFirst/connectfirst/data/2024/08/29/" + path.getFileName() + "|1725622272|Lead|1.1|unknown|inc");
                        } catch (IOException e) {
                            System.out.println("Error");
                            throw new RuntimeException(e);
                        }
                    });
        }
        fr.close();
    }
}
