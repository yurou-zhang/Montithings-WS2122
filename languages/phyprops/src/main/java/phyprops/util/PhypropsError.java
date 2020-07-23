/* (c) https://github.com/MontiCore/monticore */
package phyprops.util;

/**
 * The enum of all Phyprops errors. Extends the mixing interface {@link phyprops.util.Error}
 */
public enum PhypropsError implements phyprops.util.Error {
;

  private final String errorCode;
  private final String errorMessage;

  PhypropsError(String errorCode, String errorMessage) {
    assert (errorCode != null);
    assert (errorMessage != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * @return The unique error code of this error.
   */
  @Override
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String printErrorMessage() {
    return this.errorMessage;
  }

  @Override
  public String toString() {
    return this.getErrorCode() + ": " + this.printErrorMessage();
  }
}