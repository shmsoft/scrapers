// This script will copy S3 files from the Memex S3 to the SHMsoft S3
// To do this, it will simply call s3cmd for getting and for storing
// To run the script:
// cat <list-of-files-to-copy> | go run copy-s3-files.go

package main
import (
    "bufio"
    "fmt"
    "os"
)

func main() {

    scanner := bufio.NewScanner(os.Stdin)

    for scanner.Scan() {
        ucl := scanner.Text()

        fmt.Println(ucl)
    }

    if err := scanner.Err(); err != nil {
        fmt.Fprintln(os.Stderr, "error:", err)
        os.Exit(1)
    }
}
