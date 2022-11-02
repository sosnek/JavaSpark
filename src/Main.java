import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    static Map<String, UserUpload> uploads = new HashMap<String, UserUpload>();
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");

        get("/img", (req, res) -> GetImage(req, res));

        post("/upload", (req, res) -> PostImage(req));
    }

    public static HttpServletResponse GetImage(Request req, Response res) throws IOException {

        byte[] bytes = uploads.get(req.queryParams("imgName")).getBytes();
        HttpServletResponse raw = res.raw();
        if (bytes.length < 1) {
            raw.getOutputStream().write("Image Not found".getBytes());
        } else {
            raw.getOutputStream().write(bytes);
        }
        raw.getOutputStream().flush();
        raw.getOutputStream().close();

        return res.raw();
    }

    public static String PostImage(Request req) throws IOException {
        UserUpload upload = new UserUpload();

        upload.setImgPath(req.queryParams("img"));
        upload.setImgName(req.queryParams("name"));
        upload.setBytes(IOUtils.toByteArray((new URL(upload.getImgPath())).openStream()));
        upload.setIP(req.ip());
        if(upload.getBytes().length < 1) {
            return "Could not find img from URL";
        }
        uploads.put(upload.getImgName(), upload);
        return "Your Image has been uploaded";
    }
}