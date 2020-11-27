package de.samply.share.client.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.common.base.Joiner;
import com.google.common.collect.TreeTraverser;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.dktk.converter.PatientConverterException;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.SheetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Meaning;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Case;
import de.samply.share.model.common.Contact;
import de.samply.share.model.common.Container;
import de.samply.share.model.common.Inquiry;
import de.samply.share.model.common.Patient;
import de.samply.share.model.common.Sample;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class PatientConverterUtil {
    private static final Logger logger = LoggerFactory.getLogger(de.samply.dktk.converter.PatientConverterUtil.class);

    public PatientConverterUtil() {
    }

    public static Map<String, String> getValuesAndDesignations(MdrClient mdrClient, String mdrKey, String languageCode, boolean includeIdentical) {
        try {
            DataElement dataElement = mdrClient.getDataElement(mdrKey, languageCode);
            Validations validations = dataElement.getValidation();
            String dataType = validations.getDatatype();
            if (!dataType.equalsIgnoreCase("enumerated")) {
                return null;
            } else {
                Map<String, String> valueAndDesignationMap = new HashMap();
                List<PermissibleValue> permissibleValues = validations.getPermissibleValues();
                Iterator var9 = permissibleValues.iterator();

                label42:
                while(var9.hasNext()) {
                    PermissibleValue pv = (PermissibleValue)var9.next();
                    List<Meaning> meanings = pv.getMeanings();
                    Iterator var12 = meanings.iterator();

                    while(true) {
                        Meaning m;
                        do {
                            do {
                                if (!var12.hasNext()) {
                                    continue label42;
                                }

                                m = (Meaning)var12.next();
                            } while(!m.getLanguage().equalsIgnoreCase(languageCode));
                        } while(!includeIdentical && pv.getValue().equals(m.getDesignation()));

                        valueAndDesignationMap.put(pv.getValue(), m.getDesignation());
                    }
                }

                return valueAndDesignationMap;
            }
        } catch (MdrInvalidResponseException | ExecutionException | MdrConnectionException var14) {
            return null;
        }
    }

    public static String getValueFromSlots(List<Slot> slots, String key) {
        Iterator var2 = slots.iterator();

        Slot slot;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            slot = (Slot)var2.next();
        } while(!slot.getSlotName().trim().equalsIgnoreCase(key));

        return slot.getSlotValue();
    }

    public static Date convertDate(String dateString, SimpleDateFormat simpleDateFormat) throws ParseException {
        return simpleDateFormat.parse(dateString);
    }

    public static Workbook freezeHeaderRows(Workbook workBook) {
        for(int i = 0; i < workBook.getNumberOfSheets(); ++i) {
            Sheet sheet = workBook.getSheetAt(i);
            sheet.createFreezePane(0, 3);
        }

        return workBook;
    }

    public static String getOldestDateString(MdrClient mdrClient, Container patient, MdrIdDatatype mdrId) throws PatientConverterException {
        TreeTraverser<Container> containerTraverser = new TreeTraverser<Container>() {
            public Iterable<Container> children(Container root) {
                return root.getContainer();
            }
        };
        SimpleDateFormat dateFormat = null;

        try {
            DataElement dataElement = mdrClient.getDataElement(mdrId.getLatestMdr(), "en");
            List<Slot> dataElementSlots = dataElement.getSlots();
            String dateFormatString = getValueFromSlots(dataElementSlots, "JAVA_DATE_FORMAT");
            if (dateFormatString != null) {
                dateFormat = new SimpleDateFormat(dateFormatString);
            }
        } catch (MdrInvalidResponseException | ExecutionException | MdrConnectionException var13) {
            logger.warn("Could not get dataelement slots for " + mdrId.toString());
        }

        if (dateFormat == null) {
            return null;
        } else {
            List<Date> dates = new ArrayList();
            Iterator var15 = containerTraverser.preOrderTraversal(patient).iterator();

            while(var15.hasNext()) {
                Container container = (Container)var15.next();
                Iterator var8 = container.getAttribute().iterator();

                while(var8.hasNext()) {
                    Attribute attribute = (Attribute)var8.next();
                    MdrIdDatatype attributeKey = new MdrIdDatatype(attribute.getMdrKey());
                    if (mdrId.equalsIgnoreVersion(attributeKey)) {
                        try {
                            dates.add(convertDate((String)attribute.getValue().getValue(), dateFormat));
                        } catch (ParseException var12) {
                            throw new PatientConverterException(var12);
                        }
                    }
                }
            }

            if (dates.isEmpty()) {
                return null;
            } else {
                return dateFormat.format((Date)Collections.min(dates));
            }
        }
    }

    static boolean isLeaf(Container container) {
        List<Container> children = container.getContainer();
        if (children != null && children.size() >= 1) {
            Iterator var2 = children.iterator();

            Container c;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                c = (Container)var2.next();
            } while(c.getDesignation().equalsIgnoreCase("Sample"));

            return false;
        } else {
            return true;
        }
    }

    static int getCellIndex(String mdrKey, Row headerRow) {
        Iterator var2 = headerRow.iterator();

        Cell cell;
        do {
            if (!var2.hasNext()) {
                return -1;
            }

            cell = (Cell)var2.next();
        } while(!cell.getStringCellValue().equals(mdrKey));

        return cell.getColumnIndex();
    }

    public static Sheet sortSheetByDktkId(Workbook workbook, int sheetIndex, int prefixLength) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) {
            return sheet;
        } else {
            Map<Integer, Integer> dktkIdMap = new TreeMap();
            Row dktkIdRow = sheet.getRow(2);
            int unknownId = 10000;

            int dktkId;
            for(int i = dktkIdRow.getFirstCellNum(); i <= dktkIdRow.getLastCellNum(); ++i) {
                Cell cell = dktkIdRow.getCell(i);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    if (cellValue != null && cellValue.length() > 0) {
                        dktkId = Integer.parseInt(cellValue.substring(prefixLength));
                        dktkIdMap.put(dktkId, i);
                    } else {
                        ++unknownId;
                        dktkIdMap.put(unknownId, i);
                    }
                }
            }

            ArrayList<Integer> dktkIdList = new ArrayList(dktkIdMap.values());
            int offset = dktkIdRow.getFirstCellNum();

            for(int i = 0; i < dktkIdRow.getPhysicalNumberOfCells(); ++i) {
                dktkId = ((Integer)dktkIdList.get(i)).intValue();
                int target = i + offset;
                swapColumns(sheet, dktkId, target);
                dktkIdList.set(dktkIdList.indexOf(target), dktkId);
                dktkIdList.set(i, target);
            }

            return sheet;
        }
    }

    private static void swapColumns(Sheet sheet, int columnA, int columnB) {
        if (sheet != null) {
            if (columnA == columnB) {
                logger.trace("Both row indices are identical. No modifications needed.");
            } else if (sheet != null && columnA >= 0 && columnB >= 0) {
                Row headerRow = sheet.getRow(0);
                int maxCellNum = headerRow.getLastCellNum();
                if (maxCellNum >= columnA && maxCellNum >= columnB) {
                    CellStyle defaultCellstyle = sheet.getWorkbook().createCellStyle();

                    for(int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); ++i) {
                        Cell a = SheetUtil.getCellWithMerges(sheet, i, columnA);
                        Cell b = SheetUtil.getCellWithMerges(sheet, i, columnB);
                        if (a != null && b != null) {
                            String tmp = b.getStringCellValue();
                            CellStyle tmpStyle = b.getCellStyle();
                            b.setCellValue(a.getStringCellValue());
                            b.setCellStyle(a.getCellStyle());
                            a.setCellValue(tmp);
                            a.setCellStyle(tmpStyle);
                        } else {
                            Row row;
                            if (a == null && b != null) {
                                row = sheet.getRow(i);
                                a = row.createCell(columnA);
                                a.setCellValue(b.getStringCellValue());
                                a.setCellStyle(b.getCellStyle());
                                b.setCellType(CellType.BLANK);
                                b.setCellStyle(defaultCellstyle);
                            } else {
                                if (a == null || b != null) {
                                    continue;
                                }

                                row = sheet.getRow(i);
                                b = row.createCell(columnB);
                                b.setCellValue(a.getStringCellValue());
                                b.setCellStyle(a.getCellStyle());
                                a.setCellType(CellType.BLANK);
                                a.setCellStyle(defaultCellstyle);
                            }
                        }

                        if (i == 0) {
                            Hyperlink aHyperlink = a.getHyperlink();
                            Hyperlink bHyperlink = b.getHyperlink();
                            if (aHyperlink != null && bHyperlink != null) {
                                a.setHyperlink(bHyperlink);
                                b.setHyperlink(aHyperlink);
                            } else if (aHyperlink == null && bHyperlink != null) {
                                a.setHyperlink(bHyperlink);
                                b.removeHyperlink();
                            } else if (aHyperlink != null && bHyperlink == null) {
                                b.setHyperlink(aHyperlink);
                                a.removeHyperlink();
                            }
                        }

                        if (i == 1) {
                            Comment aComment = a.getCellComment();
                            Comment bComment = b.getCellComment();
                            if (aComment != null && bComment != null) {
                                a.setCellComment(bComment);
                                b.setCellComment(aComment);
                            } else if (aComment == null && bComment != null) {
                                a.setCellComment(bComment);
                                b.removeCellComment();
                            } else if (aComment != null && bComment == null) {
                                b.setCellComment(aComment);
                                a.removeCellComment();
                            }
                        }
                    }

                } else {
                    throw new IndexOutOfBoundsException("One of the given columns exceeds the maximum. Max=" + maxCellNum + ";a=" + columnA + ";b=" + columnB);
                }
            } else {
                throw new IllegalArgumentException("An excel sheet has to be provided and the row indices have to be positive integers");
            }
        }
    }

    public static void addCellComments(MdrClient mdrClient, Workbook workbook) {
        Iterator sheetIterator = workbook.sheetIterator();

        while(true) {
            Sheet sheet;
            do {
                do {
                    if (!sheetIterator.hasNext()) {
                        return;
                    }

                    sheet = (Sheet)sheetIterator.next();
                } while(sheet.getSheetName() == null);
            } while(sheet.getSheetName().equalsIgnoreCase("info"));

            Row keyRow = sheet.getRow(0);
            Row destinationRow = sheet.getRow(1);
            Iterator cellIterator = destinationRow.cellIterator();

            while(cellIterator.hasNext()) {
                Cell cell = (Cell)cellIterator.next();
                String mdrKey = keyRow.getCell(cell.getColumnIndex()).getStringCellValue();
                if (mdrKey != null) {
                    if (mdrKey.startsWith("urn:dktk:dataelement:43")) {
                        addCommentToCell(cell, "Entspricht dem Datum, an welchem dieses Ergebnis befundet wurde");
                    } else if (mdrKey.startsWith("urn:dktk:dataelement:50")) {
                        addCommentToCell(cell, "Gibt an, ob Probe vorhanden ist");
                    } else {
                        addValueDesignationListCommentToCell(mdrClient, cell, mdrKey);
                    }
                }
            }
        }
    }

    public static Cell addValueDesignationListCommentToCell(MdrClient mdrClient, Cell cell, String mdrKey) {
        Row row = cell.getRow();
        Sheet sheet = row.getSheet();
        Workbook workBook = sheet.getWorkbook();
        Map<String, String> valueDesignationMap = getValuesAndDesignations(mdrClient, mdrKey, "de", false);
        if (valueDesignationMap != null && !valueDesignationMap.isEmpty()) {
            Joiner.MapJoiner mapJoiner = Joiner.on('\n').withKeyValueSeparator(" -> ");
            String commentString = mapJoiner.join(valueDesignationMap);
            int listLength = valueDesignationMap.size();
            CreationHelper factory = workBook.getCreationHelper();
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex() + 3);
            anchor.setRow1(row.getRowNum());
            anchor.setRow2(row.getRowNum() + listLength);
            Comment comment = drawing.createCellComment(anchor);
            RichTextString str = factory.createRichTextString(commentString);
            comment.setAuthor("Samply.Share.Client");
            comment.setString(str);
            cell.setCellComment(comment);
            return cell;
        } else {
            return cell;
        }
    }

    public static Cell addCommentToCell(Cell cell, String commentString) {
        Row row = cell.getRow();
        Sheet sheet = row.getSheet();
        Workbook workBook = sheet.getWorkbook();
        CreationHelper factory = workBook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 3);
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum() + 2);
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(commentString);
        comment.setAuthor("Samply.Share.Client");
        comment.setString(str);
        cell.setCellComment(comment);
        return cell;
    }

    public static Inquiry createInquiryObjectForInfoSheet(String inquiryLabel, String inquiryDescription) {
        Inquiry inquiry = new Inquiry();
        inquiry.setLabel(inquiryLabel);
        inquiry.setDescription(inquiryDescription);
        return inquiry;
    }

    public static Contact createContactObjectForInfoSheet(String contactTitle, String contactFirstname, String contactLastname) {
        Contact contact = new Contact();
        contact.setTitle(contactTitle);
        contact.setFirstname(contactFirstname);
        contact.setLastname(contactLastname);
        return contact;
    }

    public static Patient removeAttributeFromPatient(Patient patient, String attributeString) {
        patient = removeAttributeFromCases(patient, attributeString);
        patient = removeAttributeFromSamples(patient, attributeString);
        return patient;
    }

    public static Patient removeAttributeFromCases(Patient patient, String attributeString) {
        Iterator caseIterator = patient.getCase().iterator();

        while(caseIterator.hasNext()) {
            Case _case = (Case)caseIterator.next();
            Iterator attributeIterator = _case.getAttribute().iterator();

            while(attributeIterator.hasNext()) {
                Attribute attribute = (Attribute)attributeIterator.next();
                if (attribute.getMdrKey().equalsIgnoreCase(attributeString)) {
                    attributeIterator.remove();
                }
            }
        }

        return patient;
    }

    public static Patient removeAttributeFromSamples(Patient patient, String attributeString) {
        Iterator sampleIterator = patient.getSample().iterator();

        while(sampleIterator.hasNext()) {
            Sample sample = (Sample)sampleIterator.next();
            Iterator attributeIterator = sample.getAttribute().iterator();

            while(attributeIterator.hasNext()) {
                Attribute attribute = (Attribute)attributeIterator.next();
                if (attribute.getMdrKey().equalsIgnoreCase(attributeString)) {
                    attributeIterator.remove();
                }
            }
        }

        return patient;
    }

    private static int getColumnIndex(Sheet sheet, String dataElementUrn) {
        Row headerRow = sheet.getRow(0);

        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); ++i) {
            Cell cell = headerRow.getCell(i);
            if (cell.getStringCellValue() != null && cell.getStringCellValue().startsWith(dataElementUrn)) {
                return cell.getColumnIndex();
            }
        }

        return -1;
    }

    protected static void autosizeAllColumns(Workbook workBook) {
        for(int i = 0; i < workBook.getNumberOfSheets(); ++i) {
            Sheet sheet = workBook.getSheetAt(i);
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for(int j = 0; j <= headerRow.getLastCellNum(); ++j) {
                    sheet.autoSizeColumn(j);
                }
            }
        }

    }

    protected static void addAutoFilter(Workbook workbook, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int rowStartIndex = 2;
        int rowEndIndex = sheet.getLastRowNum();
        int columnStartIndex = 0;
        int columnEndIndex = sheet.getRow(0).getLastCellNum() - 1;
        CellRangeAddress cra = new CellRangeAddress(rowStartIndex, rowEndIndex, columnStartIndex, columnEndIndex);
        sheet.setAutoFilter(cra);
    }

    public static boolean isBlacklisted(List<MdrIdDatatype> blacklist, MdrIdDatatype attributeKey) {
        Iterator var2 = blacklist.iterator();

        MdrIdDatatype entry;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            entry = (MdrIdDatatype)var2.next();
        } while(!entry.equalsIgnoreVersion(attributeKey));

        return true;
    }

    static Container createPatientContainer(Patient centraxxPatient) {
        Container patientContainer = new Container();
        patientContainer.getAttribute().addAll(centraxxPatient.getAttribute());
        patientContainer.getContainer().addAll(centraxxPatient.getContainer());
        return patientContainer;
    }

    public static String getFirstValueForKey(Patient patient, MdrIdDatatype key) {
        TreeTraverser<Container> containerTraverser = new TreeTraverser<Container>() {
            public Iterable<Container> children(Container root) {
                return root.getContainer();
            }
        };
        Container patientContainer = createPatientContainer(patient);
        Iterator var4 = containerTraverser.breadthFirstTraversal(patientContainer).iterator();

        while(var4.hasNext()) {
            Container container = (Container)var4.next();
            Iterator var6 = container.getAttribute().iterator();

            while(var6.hasNext()) {
                Attribute attribute = (Attribute)var6.next();
                MdrIdDatatype attributeKey = new MdrIdDatatype(attribute.getMdrKey());
                if (attributeKey.equalsIgnoreVersion(key)) {
                    return (String)attribute.getValue().getValue();
                }
            }
        }

        return null;
    }
}
