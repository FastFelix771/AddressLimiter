package de.unitygaming.addresslimiter;

import static java.lang.String.format;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Config {

	@XmlElement
	@Getter @Setter
	private int limit = 3;

	@XmlElement
	@Getter @Setter
	private String cancelReason = format("Sorry, you can only connect %d times with the same IP!", limit);

}
