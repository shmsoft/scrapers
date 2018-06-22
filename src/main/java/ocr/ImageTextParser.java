/*
 *
 * Copyright SHMsoft, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ocr;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This class parses file with or without embedded documents
 *
 * @author nehaojha
 */
public class ImageTextParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTextParser.class);
    private static final Parser AUTO_DETECT_PARSER = new AutoDetectParser();
    private static final ParseContext PARSE_CONTEXT = new ParseContext();
    private static final String EMPTY = "";

    public static String parseImage() {
        try (InputStream stream = new FileInputStream(new File("/tmp/captcha.png"))) {
            BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
            AUTO_DETECT_PARSER.parse(stream, handler, new Metadata(), PARSE_CONTEXT);
            return handler.toString();
        } catch (Exception ex) {
            LOGGER.error("Problem parsing document {}", ex);
        }
        return EMPTY;
    }
}
