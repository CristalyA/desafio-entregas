package br.com.anjun.tracker.rest.client;

import lombok.Data;

@Data
public class AddressClient {

    public String id;
    public String cep;
    public String logradouro;
    public String bairro;
    public String localidade;
    public String uf;
}
