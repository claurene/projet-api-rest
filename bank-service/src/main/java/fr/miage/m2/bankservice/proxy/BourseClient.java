package fr.miage.m2.bankservice.proxy;

import com.netflix.discovery.EurekaClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class BourseClient {

    private final RestTemplate restTemplate;

    private final EurekaClient eurekaClient;

    private final String BOURSE_URL = "/change-devise/source/{source}/cible/{cible}";

    public BourseClient(RestTemplateBuilder builder, EurekaClient eurekaClient) {
        this.restTemplate = builder.build(); // no bean needed
        this.eurekaClient = eurekaClient;
    }

    public BigDecimal getValeurDeChange(@PathVariable String source, @PathVariable String cible) {
        return this.restTemplate.getForObject(getUrl()+BOURSE_URL,BigDecimal.class,source,cible);
    }

    public BigDecimal getMontant(@PathVariable String source, @PathVariable String cible, @PathVariable BigDecimal qte) {
        return this.restTemplate.getForObject(getUrl()+BOURSE_URL+"/quantite/{qte}",BigDecimal.class,source,cible,qte);
    }

    private String getUrl(){
        return eurekaClient.getNextServerFromEureka("bourse-service",false).getHomePageUrl();
    }
}
