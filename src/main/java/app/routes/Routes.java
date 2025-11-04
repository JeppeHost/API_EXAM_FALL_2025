package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    CandidateRoutes candidateRoutes = new CandidateRoutes();
    SkillRoutes skillRoutes = new SkillRoutes();

    public EndpointGroup getRoutes() {

        return () -> {
            path("/candidates", candidateRoutes.getRoutes());
            path("/skills", skillRoutes.getRoutes());
        };
    }
}
