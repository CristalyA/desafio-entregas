package br.com.anjun.tracker.domain.model;

import br.com.anjun.tracker.domain.model.enums.DeliveryStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table
public class DeliveryPackage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    private Address address;

    private DeliveryStatus deliveryStatus;

    private String sender;

    private String recipient;

}
