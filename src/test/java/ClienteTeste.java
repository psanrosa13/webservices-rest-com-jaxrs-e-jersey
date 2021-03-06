import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.Servidor;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;


public class ClienteTeste {

	private HttpServer server;
	private Client client;
	private WebTarget target;
	
	@Before
    public void before() {
       this.server = Servidor.inicializaServidor();
       ClientConfig config = new ClientConfig();
       config.register(new LoggingFilter());
       this.client = ClientBuilder.newClient(config);
       this.target = client.target("http://localhost:8080");
    }

    @After
    public void mataServidor() {
        server.stop();
    }
	
	
	@Test
	public void testaQueAConexaoComOServidorFunciona(){
		this.target = client.target("http://www.mocky.io");
		String conteudo= target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
		Assert.assertTrue(conteudo.contains("<rua>Rua Vergueiro 3185"));
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		Carrinho carrinho= target.path("/carrinhos/1").request().get(Carrinho.class);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	
	}
	
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperadoJson() {
		Carrinho carrinho= target.path("/carrinhos/json/1").request().get(Carrinho.class);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	
	}

	
	@Test
	public void adicionarNovoCarrinhoComTablet(){
		Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
	}
	
	@Test
	public void adicionarNovoCarrinhoComChocolate(){
			
        Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(315L, "Chocolate", 10, 1));
        carrinho.setRua("Rua Treze");
        carrinho.setCidade("PG City");
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos/").request().post(entity);
				
		String conteudo = client.target(response.getLocation()).request().get(String.class);
       
		Assert.assertTrue(conteudo.contains("Chocolate"));
	}
	
}
