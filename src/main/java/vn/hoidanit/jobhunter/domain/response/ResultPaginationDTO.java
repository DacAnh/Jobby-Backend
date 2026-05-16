package vn.hoidanit.jobhunter.domain.response;

public class ResultPaginationDTO {

    // format output phân trang để trả cho client
    public static class Meta{
        //trang hiện tại
        private int page;
        //số lượng bản ghi đã lấy
        private int pageSize;
        //tổng số trang với điều kiện query
        private int pages;
        // tổng số phần tử (số bản ghi)
        private long total;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    private Meta meta;
    private Object result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
