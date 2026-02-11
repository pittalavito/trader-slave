package app.traderslave.service;

import app.traderslave.command.backtest.BackTestCloseOrderCommand;
import app.traderslave.command.backtest.BackTestCreateOrderCommand;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.service.BackTestOrderDomainService;
import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.BackTestOrderDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestOrderService {

    private final BackTestOrderDomainService orderDomainService;
    private final BackTestCreateOrderCommand createOrderCommand;
    private final BackTestCloseOrderCommand closeOrderCommand;

    public List<BackTestOrder> getAll(Long id) {
        return orderDomainService.findAllByPortfolioId(id);
    }

    public List<BackTestOrder> getAll(Long id, BackTestOrder.Status status) {
        return orderDomainService.findAllByPortfolioId(id, status);
    }

    public BackTestOrderDto create(BackTestCreateOrderReqDto dto) {
        createOrderCommand.setCommandRequest(dto);
        return createOrderCommand.execute();
    }

    @Transactional
    public BackTestOrderDto close(BackTestCloseOrderReqDto dto) {
        closeOrderCommand.setCommandRequest(dto);
        return closeOrderCommand.execute();
    }
}
