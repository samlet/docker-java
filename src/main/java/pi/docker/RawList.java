package pi.docker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
@Path(value = "/raw")
public class RawList {
    //+
    @GET
    @Produces({"application/json", "application/xml"})
    public String getRawList() {
        return "{\n" +
                "    \"type\" : \"record\",\n" +
                "    \"name\" : \"Person\",\n" +
                "    \"fields\" : [ {\n" +
                "      \"name\" : \"name\",\n" +
                "      \"type\" : \"string\"\n" +
                "    }";
    }


    //+
}
