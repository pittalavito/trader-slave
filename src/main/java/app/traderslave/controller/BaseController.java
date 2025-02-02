package app.traderslave.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public abstract class BaseController {
    private final BeanFactory beanFactory;
}
