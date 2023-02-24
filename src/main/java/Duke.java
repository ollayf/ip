import java.util.Scanner;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import messages.ErrorMessages;
import messages.OperationsMessages;
import tasks.Deadline;
import tasks.Event;
import tasks.Todo;
import exceptions.EmptyInputException;
import exceptions.InvalidCommand;

public class Duke {
    // Constants
    private static final String BOT_NAME = "Duke"; 

    private static Parser parser = new Parser();
    private static Storage storage = new Storage();
    private static TaskList tasklist = new TaskList();
    // private static Set<String> markedItems = new HashSet<String>(MAX_ITEMS);

    public static void main(String[] args) {
        
        Ui.printStartMsg();
        Ui.printResponse(String.format(OperationsMessages.INITIAL_MSG, BOT_NAME));
        Scanner scanner = new Scanner(System.in);
        try {
            storage.load(tasklist);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(ErrorMessages.LOAD_FAIL);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                handleInput(line);
            } catch (EmptyInputException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                Ui.printResponse(ErrorMessages.EMPTY_INPUT);
            } catch (InvalidCommand e) {
                Ui.printResponse(ErrorMessages.INVALID_COMMAND);
            } catch (ArrayIndexOutOfBoundsException e) {
                Ui.printResponse(ErrorMessages.INCORRECT_FIELDS);
            } catch (IOException e) {
                Ui.printResponse(ErrorMessages.LOAD_FAIL);
            }
        }
        scanner.close();
    }

    private static void handleInput(String line) throws EmptyInputException, InvalidCommand, IOException {
        int numWords = Parser.getNumWords(line);
        String command = Parser.getCommand(line);
        if (command.equals("list")) {
            printItems();
        } else if (command.equals("save")) {
            storage.save(tasklist);
            Ui.printResponse(OperationsMessages.SAVED_MSG);
        } else if (command.equals("load")) {
            storage.load(tasklist);
            Ui.printResponse(OperationsMessages.LOADED_MSG);
        } else if (numWords <= 1) {
            // dont move on if less than 2 parts
            throw new EmptyInputException();
        } else if (command.equals("bye")) {
            Ui.printResponse(OperationsMessages.BYE_MSG);
        } else if (command.equals("todo")) {
            line = line.substring(command.length() + 1);
            createTodo(line);
        } else if (command.equals("delete") || command.equals("remove")) {
            line = line.substring(command.length() + 1);
            removeItem(Parser.getInputIndex(line));
        } else if (command.equals("deadline")) {
            line = line.substring(command.length() + 1);
            createDeadline(line);
        } else if (command.equals("event")) {
            line = line.substring(command.length() + 1);
            createEvent(line);
        } else if (command.equals("list")) {
            printItems();
        } else if (command.equals("mark")) {
            // String item = line.substring(4); // if want to mark by name
            markItem(Parser.getInputIndex(line));
        } else if (command.equals("unmark")) {
            // String item = line.substring(6); // if want to unmark by name
            unmarkItem(Parser.getInputIndex(line));
        } else { // add item
            throw new InvalidCommand();
        }
    }


    private static void createTodo(String line) {
        Todo item = Parser.initTodo(line);
        tasklist.addItem(item);
        Ui.printResponse(String.format(OperationsMessages.ADDED_MSG, item.toString(), tasklist.size()));
    }

    private static void createDeadline(String line) { 
        
        Deadline item = Parser.initDeadline(line);
        tasklist.addItem(item);
        Ui.printResponse(String.format(OperationsMessages.ADDED_MSG, item.toString(), tasklist.size()));
    }

    private static void createEvent(String line) {
        
        Event item = Parser.initEvent(line);
        tasklist.addItem(item);
    }

    private static void markItem(int itemNo) {
        Todo item = tasklist.get(itemNo);
        item.setDone(true);
        Ui.printResponse("    " + OperationsMessages.MARK_MSG + "\n" + "    " + item.toString());
    }

    private static void unmarkItem(int itemNo) {
        Todo item = tasklist.get(itemNo);
        item.setDone(false);
        Ui.printResponse("    " + OperationsMessages.UNMARK_MSG + "\n" + "    " + item.toString());
    }

    private static void removeItem(int itemIdx) {
        Todo itemToDelete = tasklist.remove(itemIdx);
        Ui.printResponse(String.format(OperationsMessages.DELETED_MSG, itemToDelete.toString(), tasklist.size()));
    }

    private static void printItems() {
        String response = OperationsMessages.SHOW_ITEMS_MSG + "\n";
        for (int i = 1; i <= tasklist.size(); i++) {
            Todo item = tasklist.get(i);
            response += String.format("    %d.%s\n", i, item.toString());
        }
        Ui.printResponse(response);
    }
}
