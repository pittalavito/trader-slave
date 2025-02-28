package app.traderslave.exception.custom;

import app.traderslave.exception.model.EntityNotFoundResDto;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;

public class EntityNotFoundException extends BaseCustomException {

    private final String className;

    public EntityNotFoundException(String className) {
        super(ExceptionEnum.ENTITY_NOT_FOUND);
        this.className = className;
        //todo className non viene settato correttamente è un bug piccolo che dovrà essere risolto
    }

    @Override
    protected ExceptionResDto buildCustomResponseDto() {
        EntityNotFoundResDto dto = new EntityNotFoundResDto();
        dto.setClassName(className);
        return dto;
    }
}
