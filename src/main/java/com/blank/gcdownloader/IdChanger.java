package com.blank.gcdownloader;

import com.blank.gcdownloader.model.IdWrapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class IdChanger {

    public void renameIds(String idListFileName, String folderName, int oldColNum, int newColNum) throws IOException {
        File folder = new File(folderName);
        List<File> subFolders = Arrays.stream(Objects.requireNonNull(folder.listFiles(File::isDirectory))).toList();
        List<IdWrapper> idWrapperList = createIdObjects(idListFileName, oldColNum, newColNum);
        System.out.println("Changing IDs...");
        subFolders.forEach(subFolder -> {
            Optional<IdWrapper> idDto = idWrapperList.stream().filter(tempIdWrapper -> tempIdWrapper.oldId().equals(subFolder.getName())).findFirst();
            if (idDto.isPresent()) {
                File newFolder = new File(subFolder.getParentFile(), !idDto.get().newId().equals("") ? idDto.get().newId() : "NA - " + idDto.get().oldId());
                if (!idDto.get().oldId().equals(idDto.get().newId())) subFolder.renameTo(newFolder);
            } else {
                File newFolder = new File(subFolder.getParentFile(), "NA - " + subFolder.getName());
                boolean success = subFolder.renameTo(newFolder);
                if (success) System.out.println(subFolder.getName() + " Renamed!");
                else System.out.println(subFolder.getName() + " Renaming failed.");
            }
        });
    }

    private List<IdWrapper> createIdObjects(String filename, int oldColNum, int newColNum) throws IOException {
        List<String> oldIds = readColumn(filename, oldColNum);
        List<String> newIds = readColumn(filename, newColNum);
        List<IdWrapper> idWrapperList = new ArrayList<>();
        for (int i = 0; i < oldIds.size(); ++i) {
            idWrapperList.add(new IdWrapper(oldIds.get(i), newIds.get(i)));
        }
        return idWrapperList;
    }

    private List<String> readColumn(String filename, int colNum) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new File(filename))) {
            Sheet sheet = workbook.getSheetAt(0);
            Stream<String> stringStream = Stream.empty();
            if (sheet.getRow(0) != null) {
                stringStream = StreamSupport.stream(sheet.spliterator(), false)
                        .filter(row -> row.getCell(colNum) != null)
                        .map(row -> {
                            Cell cell = row.getCell(colNum);
                            String cellValue = "";
                            if (cell.getCellType() == CellType.NUMERIC) {
                                DataFormatter formatter = new DataFormatter();
                                cellValue = formatter.formatCellValue(cell);
                            } else if (cell.getCellType() == CellType.STRING) {
                                cellValue = cell.getStringCellValue();
                            }
                            return cellValue;
                        });
            }
            return stringStream.toList();
        }
    }

}
