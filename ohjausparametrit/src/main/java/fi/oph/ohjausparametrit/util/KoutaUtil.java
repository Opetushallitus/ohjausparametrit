package fi.oph.ohjausparametrit.util;

import org.apache.commons.lang3.StringUtils;

public class KoutaUtil {

  public static boolean isKoutaHakuOid(String oid) {
    if (StringUtils.isBlank(oid)) return false;
    if (oid.startsWith("1.2.246.562.29.") && oid.length() > 27) return true;
    return false;
  }
}
