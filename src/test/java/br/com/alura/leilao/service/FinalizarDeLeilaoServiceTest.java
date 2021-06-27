package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinalizarDeLeilaoServiceTest {

    @Mock
    private LeilaoDao leilaoDao;
    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    private FinalizarLeilaoService service;
    private Lance lanceVencedor;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    public void deveriaFinalizarUmLeilao() {
        List<Leilao> list = getListLeiloes();

        Mockito.when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(list);

        service.finalizarLeiloesExpirados();
        Leilao leilao = list.get(0);

        assertTrue(
                leilao.isFechado(),
                "O leilão não foi fechado."
        );
        assertEquals(
                new BigDecimal("2500"),
                leilao.getLanceVencedor().getValor(),
                "O valor do lance não condiz com o esperado."
        );
        Mockito.verify(leilaoDao).salvar(leilao);
    }

    @Test
    public void deveriaEnviarEmail() {
        List<Leilao> list = getListLeiloes();

        Mockito.when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(list);

        service.finalizarLeiloesExpirados();
        lanceVencedor = list.get(0).getLanceVencedor();

        assertEquals(
                "Antonio",
                lanceVencedor.getUsuario().getNome(),
                "O nome do vencedor não condiz com o esperado."
        );
        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    public void naoDeveriaEnviarEmailParaOVencedorEmCasoDeErroAoEncerrarLeilao() {
        List<Leilao> list = getListLeiloes();

        Mockito.when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(list);
        Mockito.when(leilaoDao.salvar(Mockito.any()))
                .thenThrow(RuntimeException.class);

        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        } catch (RuntimeException e) {}
    }

    private List<Leilao> getListLeiloes() {
        List<Leilao> list = new ArrayList<>();
        Leilao leilao = new Leilao(
                "Smartphone",
                new BigDecimal("1300"),
                new Usuario("José")
        );
        Lance primeiro = new Lance(
                new Usuario("Maria"),
                new BigDecimal("1800")
        );
        Lance segundo = new Lance(
                new Usuario("Antonio"),
                new BigDecimal("2500")
        );

        leilao.propoe(primeiro);
        leilao.propoe(segundo);
        list.add(leilao);

        return list;
    }
}