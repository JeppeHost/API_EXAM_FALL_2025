package app.routes;

import app.controllers.SkillController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class SkillRoutes {

    private final SkillController skillController = new SkillController();

    protected EndpointGroup getRoutes() {

        return () -> {
            //TODO: tilføj de korrekte roller
            post("/", skillController::create, Role.ANYONE);
            get("/", skillController::readAll, Role.ANYONE);
            get("/{id}", skillController::read, Role.ANYONE);
            put("/{id}", skillController::update, Role.ANYONE);
            delete("/{id}", skillController::delete, Role.ANYONE);
        };
    }
}