package application.grid.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class LoggerService {

    public void info(String flow, Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("[{}] - " + message, prependArgs(flow, args));
    }

    public void warn(String flow, Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn("[{}] - " + message, prependArgs(flow, args));
    }

    public void error(String flow, Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("[{}] - " + message, prependArgs(flow, args));
    }

    public void debug(String flow, Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug("[{}] - " + message, prependArgs(flow, args));
    }

    public void infoWithBody(String flow, Class<?> clazz, String message, Object content, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);

        Object[] allArgs = Stream.concat(
                Stream.of(prependArgs(flow, args)), // flow + args
                Stream.of(content)                  // content no final
        ).toArray();
        logger.info("[{}] - " + message + " | Content: {}", prependArgs(flow, args), content);
    }

    private Object[] prependArgs(String flow, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = flow;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }
}
