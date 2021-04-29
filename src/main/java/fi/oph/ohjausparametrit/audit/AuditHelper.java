package fi.oph.ohjausparametrit.audit;

import fi.vm.sade.auditlog.Logger;

public class AuditHelper implements Logger {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuditHelper.class);

  @Override
  public void log(String msg) {
    logger.info(msg);
  }
}
