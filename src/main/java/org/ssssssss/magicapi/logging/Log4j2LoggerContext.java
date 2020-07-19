package org.ssssssss.magicapi.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Log4j2LoggerContext implements MagicLoggerContext{

	@Override
	public void generateAppender() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();
		LoggerConfig logger = configuration.getRootLogger();
		PatternLayout layout = PatternLayout.newBuilder()
				.withCharset(StandardCharsets.UTF_8)
				.withConfiguration(configuration)
				.withPattern("%d %t %p %X{TracingMsg} %c - %m%n")
				.build();
		MagicLog4jAppender appender = new MagicLog4jAppender("Magic", logger.getFilter(), layout);
		appender.start();
		configuration.addAppender(appender);
		logger.addAppender(appender,logger.getLevel(),logger.getFilter());
		context.updateLoggers(configuration);
	}

	class MagicLog4jAppender extends AbstractAppender{

		MagicLog4jAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
			super(name, filter, layout);
		}

		@Override
		public void append(LogEvent event) {
			LogInfo logInfo = new LogInfo();
			logInfo.setLevel(event.getLevel().name().toLowerCase());
			logInfo.setMessage(event.getMessage().getFormattedMessage());
			ThrowableProxy throwableProxy = event.getThrownProxy();
			if(throwableProxy != null){
				logInfo.setThrowable(throwableProxy.getThrowable());
			}
			println(logInfo);
		}
	}
}