package pi.docker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
@Path("/meta")
public class MetaServiceList {
    @GET
    @Path("images/{image}/")
    @Produces({"application/json", "application/xml"})
    public String getItem(@PathParam("image") String image) throws Exception {
        return AppModule.meta().processServiceLabels(image);
        // return "[\""+image+"\"]";
    }
}
