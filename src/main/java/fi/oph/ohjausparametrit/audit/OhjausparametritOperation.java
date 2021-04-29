package fi.oph.ohjausparametrit.audit;

import fi.vm.sade.auditlog.Operation;

public enum OhjausparametritOperation implements Operation {
  CREATE,
  UPDATE,
  DELETE
}
