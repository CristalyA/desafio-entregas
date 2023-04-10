package br.com.anjun.tracker.domain.repository;

import br.com.anjun.tracker.domain.model.DeliveryPackage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class PackageRepository implements PanacheRepository<DeliveryPackage> {

    public List<DeliveryPackage> findBySender(String sender) {
        return list("sender",sender);
    }

    public List<DeliveryPackage> findByRecipient(String recipient) {
        return list("recipient",recipient);
    }

//    public Optional<DeliveryPackage> findBySenderAndRecipient(String sender, String recipient) {
//        return find("recipient",recipient);
//    }TODO
}
