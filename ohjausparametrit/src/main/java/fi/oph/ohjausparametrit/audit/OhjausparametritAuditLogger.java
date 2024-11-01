package fi.oph.ohjausparametrit.audit;

import fi.oph.ohjausparametrit.configurations.ConfigEnums;
import fi.vm.sade.auditlog.*;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class OhjausparametritAuditLogger extends Audit {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditHelper.class);

  OhjausparametritAuditLogger() {
    super(new AuditHelper(), ConfigEnums.SERVICENAME.value(), ApplicationType.VIRKAILIJA);
  }

  public void log(Operation operation, Target target, Changes changes) {
    log(getUser(), operation, target, changes);
  }

  private User getUser() {
    ServletRequestAttributes sra =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    InetAddress address = getInetAddress(sra);
    if (address == null) {
      return null;
    }

    String session = "";
    String userAgent = "";
    if (sra != null) {
      HttpServletRequest req = sra.getRequest();
      session = req.getSession().getId();
      userAgent = req.getHeader("User-Agent");
    }

    return new User(getCurrentPersonOid(), address, session, userAgent);
  }

  private InetAddress getInetAddress(ServletRequestAttributes sra) {
    try {
      if (sra != null) {
        HttpServletRequest request = sra.getRequest();

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null) {
          return InetAddress.getByName(realIp);
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
          return InetAddress.getByName(forwardedFor);
        }
        logger.warn(
            "X-Real-IP or X-Forwarded-For was not set. Defaulting to Request.getRemoteAddr().");

        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null) {
          return InetAddress.getByName(remoteAddr);
        }
        logger.warn("RemoteAddr was null. Defaulting to localhost/127.0.0.1.");
      }
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      logger.error("Error creating InetAddress: ", e);
      return null;
    }
  }

  private Oid getCurrentPersonOid() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      try {
        return new Oid(authentication.getName());
      } catch (GSSException e) {
        logger.error("Error creating Oid-object out of {}", authentication.getName());
      }
    }
    return null;
  }
}
