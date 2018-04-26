package main

import (
	"fmt"
	"bufio"
	"os"
	"strings"
	"sync"
	"os/exec"
)

func main() {

	fileHandle, _ := os.Open("file-list.txt")
	defer fileHandle.Close()
	fileScanner := bufio.NewScanner(fileHandle)
	for fileScanner.Scan() {
		line := fileScanner.Text();
		executeCommandsFor(line);
	}
}

func executeCommandsFor(line string) {
	wg := new(sync.WaitGroup)
	fmt.Println("Working with " + line);
	strArr := strings.Split(line, "/");
	fileName := strArr[len(strArr) - 1];
	fmt.Println(fileName);

	downloadCommand := "s3cmd -c .s3cfg-memex get " + line;
	wg.Add(1)
	go exe_cmd(downloadCommand, wg)
	wg.Wait()

	uploadCommand := "s3cmd -c .s3cfg put " + fileName + " s3://shmsoft/test-data/";
	wg.Add(1)
	go exe_cmd(uploadCommand, wg)
	wg.Wait()

	removeCommand := "rm " + fileName;
	wg.Add(1)
	go exe_cmd(removeCommand, wg)
	wg.Wait()

}

func exe_cmd(cmd string, wg *sync.WaitGroup) {
	fmt.Println("\nExecuting... " + cmd)
	out, err := exec.Command("bash", "-c", cmd).CombinedOutput()
	if err != nil {
		fmt.Println("error occured")
		fmt.Printf("%s", err)
		panic(err)
	}
	fmt.Printf("%s", out)
	wg.Done()
}