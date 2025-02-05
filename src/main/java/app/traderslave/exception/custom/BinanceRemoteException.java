package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;

public class BinanceRemoteException extends BaseCustomException {

    private final String message;
    private final String url;

    public BinanceRemoteException(String message, String url) {
        super(ExceptionEnum.BINANCE_REMOTE);
        this.message = message;
        this.url = url;
    }

    @Override
    protected ExceptionResponseDto buildCustomResponseDto() {
        ExceptionResponseDto exResponseDto = new ExceptionResponseDto();
        exResponseDto.setHttpStatus(ExceptionEnum.BINANCE_REMOTE.getHttpStatus());
        exResponseDto.setMessage(url + " " + message);
        return exResponseDto;
    }
}
