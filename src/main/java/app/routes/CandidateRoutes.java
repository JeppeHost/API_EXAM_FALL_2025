package app.routes;

import app.controllers.CandidateController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoutes {

    private final CandidateController candidateController = new CandidateController();

    protected EndpointGroup getRoutes() {

        return () -> {
            //TODO: tilføj de korrekte roller
            post("/", candidateController::create, Role.ANYONE);
            get("/", candidateController::readAll, Role.ANYONE);
            get("/{id}", candidateController::read, Role.ANYONE);
            put("/{id}", candidateController::update, Role.ANYONE);
            delete("/{id}", candidateController::delete, Role.ANYONE);

        };
    }
}