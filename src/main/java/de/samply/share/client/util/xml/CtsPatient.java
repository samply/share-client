package de.samply.share.client.util.xml;

/**
 * Patient bean for Mainzelliste.
 */
public class CtsPatient {

  private String vorname;
  private String nachname;
  private String geburtsname;
  private Integer geburtstag;
  private Integer geburtsmonat;
  private Integer geburtsjahr;
  private String adresseStadt;
  private String adressePlz;
  private String adresseStrasse;
  private String versicherungsnummer;

  public String getVorname() {
    return vorname;
  }

  public void setVorname(String vorname) {
    this.vorname = vorname;
  }

  public String getNachname() {
    return nachname;
  }

  public void setNachname(String nachname) {
    this.nachname = nachname;
  }

  public String getGeburtsname() {
    return geburtsname;
  }

  public void setGeburtsname(String geburtsname) {
    this.geburtsname = geburtsname;
  }

  public Integer getGeburtstag() {
    return geburtstag;
  }

  public void setGeburtstag(Integer geburtstag) {
    this.geburtstag = geburtstag;
  }

  public Integer getGeburtsmonat() {
    return geburtsmonat;
  }

  public void setGeburtsmonat(Integer geburtsmonat) {
    this.geburtsmonat = geburtsmonat;
  }

  public Integer getGeburtsjahr() {
    return geburtsjahr;
  }

  public void setGeburtsjahr(Integer geburtsjahr) {
    this.geburtsjahr = geburtsjahr;
  }

  public String getAdresseStadt() {
    return adresseStadt;
  }

  public void setAdresseStadt(String adresseStadt) {
    this.adresseStadt = adresseStadt;
  }

  public String getAdressePlz() {
    return adressePlz;
  }

  public void setAdressePlz(String adressePlz) {
    this.adressePlz = adressePlz;
  }

  public String getAdresseStrasse() {
    return adresseStrasse;
  }

  public void setAdresseStrasse(String adresseStrasse) {
    this.adresseStrasse = adresseStrasse;
  }

  public String getVersicherungsnummer() {
    return versicherungsnummer;
  }

  public void setVersicherungsnummer(String versicherungsnummer) {
    this.versicherungsnummer = versicherungsnummer;
  }

  public boolean hasAdresseStrasse() {
    return adresseStrasse != null && ! adresseStrasse.isEmpty();
  }

  public boolean hasAdresseStadt() {
    return adresseStadt != null && ! adresseStadt.isEmpty();
  }

  public boolean hasAdressePlz() {
    return adressePlz != null && ! adressePlz.isEmpty();
  }
}
