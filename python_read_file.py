from PyPDF2 import PdfFileReader
import sys
import re

def extract_information(pdf_path):
    with open(pdf_path, 'rb') as f:
        pdf = PdfFileReader(f)
        number_of_pages = pdf.getNumPages()
        for part in range(number_of_pages):
            page = pdf.getPage(part)
            text = page.extractText()
            text_list = text.split("MJ Cena v Kč")
            to_cut = text_list[1]
            values = re.findall(r"([0-9]KS \D*)|([0-9].?[0-9]* KG \D*)", to_cut)
            number_pieces = 0
            only_name = ""

            for part in values:
                if part[0] != '':
                    number_pieces = re.search(r'\d*', part[0]).group()
                    only_name = part[0][4:-2]
                    print(only_name)
                    print(number_pieces)

                else:
                    number_pieces = 1
                    only_name = re.sub(r"[0-9].?[0-9]* KG", r"", part[1])[1:-2]
                    print(only_name)
                    print(number_pieces)

    return

if __name__ == '__main__':
    number_arguments = len(sys.argv)
    if number_arguments == 2:
        path = sys.argv[1]
        extract_information(path)
