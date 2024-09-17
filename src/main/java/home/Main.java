package home;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String command = scanner.nextLine();
            if (command.equals("0")) {
                break;
            }
            System.out.println("Введите путь к файлу/директории: ");
            String enteredPath = scanner.nextLine();
            Path path = Paths.get(enteredPath);
            String newName = path.getFileName().toString();
            switch (command) {
                case "1" -> getFilesListFromDir(path);
                case "2" -> makeNewDir(path, newName);
                case "3" -> makeNewFile(path, newName);
                case "4" -> renameFileOrDir(path, scanner);
                case "5" -> deleteFile(path, newName);
                default -> System.err.println("Извините, такой команды пока нет.");
            }
        }
    }

    private static void deleteFile(Path path, String newName) {
        if (!Files.isDirectory(path)) {
            if (Files.exists(path)) {
                try {
                    Files.deleteIfExists(path);
                    System.out.printf("файл %s успешно удалён\n", newName);
                } catch (IOException e) {
                    System.err.println("Произошла ошибка при удалении файла. " + e.getMessage());
                }
            } else {
                System.err.printf("файла с именем %s не существует\n", newName);
            }
        } else {
            System.err.println("С помощью этой команды можно удалить только файл!");
        }
    }

    private static void renameFileOrDir(Path path, Scanner scanner) {
        System.out.println("Введите новое имя файла/директории: ");
        String oldName = path.getFileName().toString();
        String inputName = scanner.nextLine();
        if (!Files.isDirectory(path)) {
            if (!inputName.contains(".")) {
                String oldFileExtension = oldName.substring(oldName.lastIndexOf("."));
                inputName = inputName + oldFileExtension;
            }
        }
        Path basePath = path.getParent();
        Path newPath = basePath.resolve(inputName);
        if (Files.exists(newPath)) {
            System.err.printf("файл с именем %s уже существует\n", inputName);
        } else {
            try {
                Files.move(path, newPath);
                System.out.printf("файл %s был переименован на %s\n", oldName, inputName);
            } catch (IOException e) {
                System.err.println("Произошла ошибка при переименовании файла/директории. "
                        + e.getMessage());
            }
        }
    }

    private static void makeNewFile(Path path, String newName) {
        if (Files.exists(path)) {
            System.err.printf("файл с именем %s уже существует\n", newName);
            return;
        }
        try {
            System.out.printf("создаём файл с именем: %s ...", newName);
            Files.createFile(path);
            System.out.printf("файл %s, успешно создан \n", path);
        } catch (IOException e) {
            System.err.println("Произошла ошибка при создании файла. " + e.getMessage());
        }
    }

    private static void makeNewDir(Path path, String newName) {
        if (Files.notExists(path)) {
            System.out.printf("создаём директорию с именем: %s ...\n", newName);
            try {
                Files.createDirectories(path);
                System.out.printf("директория %s, по адресу %s, успешно создана \n", newName, path);
            } catch (IOException e) {
                System.err.println("Произошла ошибка при создании директории. " + e.getMessage());
            }
        } else {
            System.err.printf("директорию с именем: %s, уже сущестует\n", newName);
        }
    }

    private static void getFilesListFromDir(Path path) {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                String dirName = path.getFileName().toString();
                int fileCount = 0;
                int dirCount = 0;
                for (File file : Objects.requireNonNull(path.toFile().listFiles())) {
                    if (file.isDirectory()) dirCount++;
                    else if (file.isFile()) fileCount++;
                }
                System.out.printf("директория %s содержит: директорий %d; файлов %d:\n", dirName, dirCount, fileCount);
                try (Stream<Path> paths = Files.list(path)) {
                    paths.map(Path::getFileName).forEach(System.out::println);
                } catch (IOException e) {
                    System.err.println("Произошла ошибка при запросе содержимого директории.");
                }
            } else {
                System.err.println("это не директория");
            }
        } else {
            System.err.println("нет такого пути");
        }
    }

    public static void printMenu() {
        System.out.println("==========Что вы хотите сделать?==========");
        System.out.println("1 - (ls) посмотреть содержимое директории.");
        System.out.println("2 - (mkdir) создать директорию.");
        System.out.println("3 - (touch) создать файл.");
        System.out.println("4 - (rename) переименовать директорию/файл.");
        System.out.println("5 - (rm_file) удалить файл.");
        System.out.println("0 - выход.");
        System.out.println("===========================================");
    }
}