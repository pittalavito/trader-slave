package app.traderslave.service;

import app.traderslave.command.backtest.BackTestCloseOrderCommand;
import app.traderslave.command.backtest.BackTestCreateOrderCommand;
import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.BackTestOrderDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestOrderService {

    private final BackTestCreateOrderCommand createOrderCommand;
    private final BackTestCloseOrderCommand closeOrderCommand;

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
