package br.com.anjun.tracker.rest;

import br.com.anjun.tracker.domain.model.Address;
import br.com.anjun.tracker.domain.model.DeliveryPackage;
import br.com.anjun.tracker.domain.model.enums.DeliveryStatus;
import br.com.anjun.tracker.domain.repository.PackageRepository;
import br.com.anjun.tracker.rest.client.AddressClient;
import br.com.anjun.tracker.rest.client.ViaCepClient;
import br.com.anjun.tracker.rest.dto.CreateTrackerRequest;
import br.com.anjun.tracker.rest.dto.ResponseError;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/delivery")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TrackerResource {

    private PackageRepository repository;
    private Validator validator;
    private ViaCepClient viaCepService;

    @Inject
    public TrackerResource(PackageRepository repository,
                           ViaCepClient viaCepService,
                           Validator validator) {
        this.repository = repository;
        this.viaCepService = viaCepService;
        this.validator = validator;
    }

    @POST
    @SneakyThrows
    @Transactional
    public Response createTracker(CreateTrackerRequest trackerRequest) {
        Set<ConstraintViolation<CreateTrackerRequest>> violations = validator.validate(trackerRequest);
        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        AddressClient addressClient = viaCepService.getAddressByCep(trackerRequest.getZipCode()).toCompletableFuture().get();
        Address address = Address.builder()
                .city(addressClient.getLocalidade())
                .neighborhood(addressClient.getBairro())
                .state(addressClient.getUf())
                .zipCode(addressClient.getCep())
                .street(addressClient.getLogradouro())
                .complement(trackerRequest.getComplementAddress())
                .number(trackerRequest.getNumberAddress())
                .build();

        DeliveryPackage pack = new DeliveryPackage();
        pack.setRecipient(trackerRequest.getRecipient());
        pack.setSender(trackerRequest.getSender());
        pack.setDeliveryStatus(DeliveryStatus.PENDING);
        pack.setAddress(address);

        repository.persist(pack);


        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(pack)
                .build();
    }

    @GET
    public Response findByQuery(@QueryParam("sender") String sender, @QueryParam("recipient") String recipient) {
        List<DeliveryPackage> pack = new ArrayList<>();

        if (recipient == null && sender == null) {
            pack = repository.findAll().list();
        } else {
            if (recipient != null && !recipient.isEmpty()) {
                pack = repository.findByRecipient(recipient);
            } else if (!Objects.isNull(sender) && !sender.isEmpty()) {
                pack = repository.findBySender(sender);
            }
        }

        return Response.ok(pack).build();
    }

    @GET
    @Path("{id}")
    @Transactional
    public Response findById(@PathParam("id") Long id) {
        return Response.ok(repository.findById(id)).build();
    }


    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        DeliveryPackage pack = repository.findById(id);

        if (pack != null) {
            repository.delete(pack);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}/status")
    @Transactional
    public Response update(@PathParam("id") Long id, DeliveryStatus deliveryStatus) {
        DeliveryPackage pack = repository.findById(id);

        if (pack == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (pack.getDeliveryStatus() == DeliveryStatus.DELIVERED || pack.getDeliveryStatus() == DeliveryStatus.CANCELED) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        pack.setDeliveryStatus(deliveryStatus);

        return Response.noContent().build();
    }

}
