package br.com.mattec.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.mattec.leilao.dao.PagamentoDao;
import br.com.mattec.leilao.model.Lance;
import br.com.mattec.leilao.model.Leilao;
import br.com.mattec.leilao.model.Pagamento;
import br.com.mattec.leilao.model.Usuario;


class GeradorDePagamentoTest {
	
	
	private GeradorDePagamento gerador;
	
	@Mock
	private PagamentoDao pagamentoDao;
	
	@Captor
	private ArgumentCaptor<Pagamento> captor;
	
	@Mock
	private Clock clock;
	
	
	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamentoDao, clock);
	}

	@Test
	void deveriaCriarPagamentoParaVencedorDoLeilao() {
		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();
		
		
		 LocalDate data = LocalDate.of(2021, 10, 15);
		 
		 Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault())
;		
		gerador.gerarPagamento(vencedor);
		
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		
		Pagamento pagamento = captor.getValue();
		
		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(vencedor.getValor(), pagamento.getValor());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao, pagamento.getLeilao());
	}
	

	private Leilao  leilao(){
		
		
		Leilao  leilao = new Leilao("Celular",
		new BigDecimal("500"),
		new Usuario("Fulano"));
		
		
		Lance paga = new Lance(new Usuario("Ciclano"),
				new BigDecimal("900"));
		
		leilao.propoe(paga);
		
		leilao.setLanceVencedor(paga);
		
		return leilao;
	}

}
