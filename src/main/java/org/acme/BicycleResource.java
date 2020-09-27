package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.acme.model.Bicycle;
import org.acme.repository.BicycleRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.*;

@Path("/api/v1/bicycle")
@RequestScoped
public class BicycleResource {

    private final static Logger LOGGER = Logger.getLogger(BicycleResource.class.getName());

    private final BicycleRepository bicycleRepository;

    @Inject
    public BicycleResource(BicycleRepository bicycleRepository) {
        this.bicycleRepository = bicycleRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Bicycle> getAllBicycle() {
        return this.bicycleRepository.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> saveBike(@Valid Bicycle bicycle) {
        return this.bicycleRepository.save(bicycle)
                .map(id -> created(URI.create("/bicycle/" + id)).build());
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getBikeById(@PathParam("id") final Integer id) {
        return this.bicycleRepository.findById(id)
                .map(data -> {
                    if (data == null) {
                        return null;
                    }
                    return ok(data).build();
                })
                .onItem().ifNull().continueWith(status(Status.NOT_FOUND).build());
        //.onFailure(PostNotFoundException.class).recoverWithItem(status(Status.NOT_FOUND).build());
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> updateBike(@PathParam("id") final Integer id, @Valid Bicycle bicycle) {
        return this.bicycleRepository.update(id, bicycle)
                .map(updated -> updated > 0 ? Status.NO_CONTENT : Status.NOT_FOUND)
                .map(status -> status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return this.bicycleRepository.delete(id)
                .map(deleted -> deleted > 0 ? Status.NO_CONTENT : Status.NOT_FOUND)
                .map(status -> status(status).build());
    }

}
