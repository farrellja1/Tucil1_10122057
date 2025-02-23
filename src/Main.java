import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

class Input {
    public static int n, m, p;
    public static String s;
    public static List<Piece> pieces = new ArrayList<Piece>();
}

class Piece {
    private String name;
    private String[] shape;
    private String color;

    public Piece(String name, String[] shape, String color) {
        this.name = name;
        this.shape = shape;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String[] getShape() {
        return shape;
    }

    public String getColor() {
        return color;
    }

    public String[] rotate() {
        int rows = shape.length;
        int cols = shape[0].length();
        String[] rotated = new String[cols];

        for (int i = 0; i < cols; i++) {
            StringBuilder sb = new StringBuilder(rows);
            for (int j = rows - 1; j >= 0; j--) {
                sb.append(shape[j].charAt(i));
            }
            rotated[i] = sb.toString();
        }
        return rotated;
    }

    public String[] flip() {
        String[] flipped = new String[shape.length];
        for (int i = 0; i < shape.length; i++) {
            flipped[i] = new StringBuilder(shape[i]).reverse().toString();
        }
        return flipped;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        for (String line : shape) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}

class Board {
    private char[][] grid;
    private Stack<Piece> pieceStack;
    private int caseCount;

    public Board(int n, int m) {
        grid = new char[n][m];
        pieceStack = new Stack<>();
        caseCount = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                grid[i][j] = '.'; // Initialize with '.' to represent empty spaces
            }
        }
    }

    public boolean placePiece(Piece piece, int row, int col) {
        String[] shape = piece.getShape();
        int pieceHeight = shape.length;
        int pieceWidth = shape[0].length();

        if (row < 0 || col < 0 || row + pieceHeight > grid.length || col + pieceWidth > grid[0].length) {
            return false; 
        }

        for (int i = 0; i < pieceHeight; i++) {
            for (int j = 0; j < pieceWidth; j++) {
                if (shape[i].charAt(j) != ' ' && grid[row + i][col + j] != '.' && shape[i].charAt(j) != '.') {
                    return false;
                }
            }
        }

        for (int i = 0; i < pieceHeight; i++) {
            for (int j = 0; j < pieceWidth; j++) {
                if (shape[i].charAt(j) != ' ') {
                    grid[row + i][col + j] = shape[i].charAt(j);
                }
            }
        }

        pieceStack.push(piece);
        return true;
    }

    public void removePiece(Piece piece, int row, int col) {
        String[] shape = piece.getShape();
        int pieceHeight = shape.length;
        int pieceWidth = shape[0].length();

        for (int i = 0; i < pieceHeight; i++) {
            for (int j = 0; j < pieceWidth; j++) {
                if (shape[i].charAt(j) != ' ') {
                    grid[row + i][col + j] = '.';
                }
            }
        }
    }

    public boolean isFilled() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '.') {
                    return false; // Found an empty space
                }
            }
        }
        return true; // No empty spaces found
    }

    public void printColoredBoard() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                char pieceChar = grid[i][j];
                if (pieceChar != '.') {
                    System.out.print(getColorForPiece(pieceChar) + pieceChar + "\u001B[0m ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    private String getColorForPiece(char pieceChar) {
        String[] colors = {
            "\u001B[31m", // Red
            "\u001B[32m", // Green
            "\u001B[33m", // Yellow
            "\u001B[34m", // Blue
            "\u001B[35m", // Magenta
            "\u001B[36m", // Cyan
            "\u001B[37m", // White
            "\u001B[90m", // Bright Black
            "\u001B[91m", // Bright Red
            "\u001B[92m", // Bright Green
            "\u001B[93m", // Bright Yellow
            "\u001B[94m", // Bright Blue
            "\u001B[95m", // Bright Magenta
            "\u001B[96m", // Bright Cyan
            "\u001B[97m"  // Bright White
        };

        int index = (pieceChar - 'A') % colors.length;
        return colors[index];
    }

    public boolean backtrack(int pieceIndex) {
        caseCount++;
        if (pieceIndex >= Input.pieces.size()) {
            return isFilled();
        }

        Piece currentPiece = Input.pieces.get(pieceIndex);
        String[] originalShape = currentPiece.getShape();

        for (int rotation = 0; rotation < 4; rotation++) {
            String[] shapeToTry = originalShape;

            if (rotation == 1) {
                shapeToTry = currentPiece.rotate();
            } else if (rotation == 2) {
                shapeToTry = currentPiece.flip();
            } else if (rotation == 3) {
                shapeToTry = currentPiece.rotate();
                shapeToTry = new Piece(currentPiece.getName(), shapeToTry, currentPiece.getColor()).rotate(); 
            }

            for (int row = 0; row < Input.n; row++) {
                for (int col = 0; col < Input.m; col++) {
                    if (placePiece(new Piece(currentPiece.getName(), shapeToTry, currentPiece.getColor()), row, col)) {
                        if (backtrack(pieceIndex + 1)) {
                            return true;
                        }
                        removePiece(new Piece(currentPiece.getName(), shapeToTry, currentPiece.getColor()), row, col);
                    }
                }
            }
        }

        return false;
    }

    public int getCaseCount() {
        return caseCount;
    }

    public char[][] getGrid() {
        return grid;
    }
}

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Scanner input = new Scanner(System.in);
        System.out.println("Enter txt file name (without path):");
        String fileName = input.next();
        
        String filePath = "../test/" + fileName;

        try {
            File file = new File(filePath);
            Scanner fileScanner = new Scanner(file);

            if (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.trim().split("\\s+");

                if (parts.length >= 3) {
                    Input.n = Integer.parseInt(parts[0]);
                    Input.m = Integer.parseInt(parts[1]);
                    Input.p = Integer.parseInt(parts[2]);

                    if (Input.n <= 0 || Input.m <= 0 || Input.p <= 0) {
                        System.out.println("Error: N, M, and P must be positive integers.");
                        return;
                    }
                } else {
                    System.out.println("Error: The first line must contain at least three values for N, M, and P.");
                    return;
                }
            }

            if (fileScanner.hasNextLine()) {
                Input.s = fileScanner.nextLine().trim();
                if (!Input.s.equals("DEFAULT") && !Input.s.equals("CUSTOM") && !Input.s.equals("PYRAMID")) {
                    System.out.println("Error: Case type must be one of DEFAULT, CUSTOM, or PYRAMID.");
                    return;
                }
            }

            while (fileScanner.hasNextLine()) {
                String pieceLine = fileScanner.nextLine().trim();

                if (pieceLine.isEmpty()) {
                    continue;
                }

                String pieceName = pieceLine;
                String[] shape = new String[]{pieceLine};

                String color = "\u001B[0m"; // Default color
                Input.pieces.add(new Piece(pieceName, shape, color));
            }

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            return;
        }

        Board board = new Board(Input.n, Input.m);
        System.out.println("Initial Board:");
        board.printColoredBoard();

        long searchStartTime = System.currentTimeMillis();
        boolean allPiecesPlaced = board.backtrack(0);
        long searchEndTime = System.currentTimeMillis();

        if (allPiecesPlaced) {
            System.out.println("All pieces placed successfully.");
            System.out.println("Final Board:");
            board.printColoredBoard();
        } else {
            System.out.println("Failed to place all pieces.");
        }

        System.out.println("Waktu pencarian: " + (searchEndTime - searchStartTime) + " ms");
        System.out.println("Banyak kasus yang ditinjau: " + board.getCaseCount());

        System.out.println("Apakah anda ingin menyimpan solusi? (ya/tidak)");
        String saveResponse = input.next().trim().toLowerCase();
        if (saveResponse.equals("ya")) {
            try {
                FileWriter writer = new FileWriter("solution.txt");
                for (int i = 0; i < board.getGrid().length; i++) {
                    for (int j = 0; j < board.getGrid()[0].length; j++) {
                        writer.write(board.getGrid()[i][j]);
                    }
                    writer.write("\n");
                }
                writer.close();
                System.out.println("Solusi telah disimpan dalam file solution.txt");
            } catch (IOException e) {
                System.out.println("Error: Gagal menyimpan solusi.");
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Total waktu eksekusi: " + elapsedTime + " milliseconds");
    }
}