package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDao;
    @Mock
    private Clock clock;
    @Captor
    private ArgumentCaptor<Pagamento> captor;

    private GeradorDePagamento geradorDePagamento;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDao, clock);
    }

    @Test
    public void deveriaGerarPagamento() {
        Leilao leilao = getLeilao();
        Lance lanceVencedor = leilao.getLanceVencedor();
        LocalDate data = LocalDate.of(2020, 12, 7);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        assertEquals(
                LocalDate.now().plusDays(1),
                pagamento.getVencimento(),
                "O data não condiz com o esperado."
        );
        assertEquals(
                lanceVencedor.getValor(),
                pagamento.getValor(),
                "O valor não condiz com o esperado."
        );
        assertEquals(
                lanceVencedor.getUsuario(),
                pagamento.getUsuario(),
                "O vencedor não condiz com o esperado."
        );
        assertEquals(
                leilao,
                pagamento.getLeilao(),
                "O leilão não condiz com o esperado."
        );
        assertFalse(pagamento.getPago());
    }

    private Leilao getLeilao() {
        Leilao leilao = new Leilao(
                "Smartphone",
                new BigDecimal("1300"),
                new Usuario("José")
        );
        Lance primeiro = new Lance(
                new Usuario("Maria"),
                new BigDecimal("1800")
        );
        leilao.propoe(primeiro);
        leilao.setLanceVencedor(primeiro);

        return leilao;
    }
}