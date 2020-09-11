package logic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.BoardDao;
import dao.ItemDao;
import dao.SaleDao;
import dao.SaleItemDao;
import dao.UserDao;

@Service
public class ShopService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private BoardDao boardDao;

	private Map<String,Object> map = new HashMap<>();

	public List<Item> getItemList() {		
		return itemDao.list();
	}

	public Item getItem(Integer id) {
		return itemDao.selectOnde(id);
	}

	public void itemCreate(Item item, HttpServletRequest request) {
		//업로드된 파일이 있을경우
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {
			uploadFileCreate(item.getPicture(),request,"item/img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		itemDao.insert(item);
	}

	private void uploadFileCreate(MultipartFile picture, HttpServletRequest request, String path) {
		//picture: 파일의 내용 저장
		String orgFile = picture.getOriginalFilename();
		String uploadPath=request.getServletContext().getRealPath("/")
				+ path;  
		System.out.println(uploadPath);
		File fpath = new File(uploadPath);
		if(fpath.exists()) fpath.mkdirs();
		try {
			//파일의 내용을 실제 파일로 저장
			picture.transferTo(new File(uploadPath + orgFile));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void itemUpdate(Item item, HttpServletRequest request) {
		//업로드된 파일이 있을경우
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {
			uploadFileCreate(item.getPicture(),request,"item/img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		itemDao.update(item); 
	}
	public void itemDelete(String id) {		
		itemDao.delete(id); 
	}

	public void userInsert(User user) {
		userDao.insert(user);
	}

	public User getUser(String userid) {
		return userDao.selectOne(userid);
	}

	/*
	 * db에 sale정보와 saleitem 정보 저장.
	 * 저장된 내용을 Sale 객체로 리턴
	 * 1. sale 테이블의 saleid 값을 설정 => 최대값 + 1
	 * 2. sale의 내용 설정. => insert
	 * 3. Cart 정보(itemSet) SaleItem 내용설정 => insert
	 * 4. 모든 정보를 Sale 객체로 저장
	 */
	public Sale checkend(User loginUser, Cart cart) {
		Sale sale = new Sale();
		int maxno = saleDao.getMaxSaleid();
		sale.setSaleid(++maxno);
		sale.setUser(loginUser);
		sale.setUserid(loginUser.getUserid());
		saleDao.insert(sale);
		//장바구니에서 판매 상품 정보
		List<ItemSet> itemList = cart.getItemSetList();	//Cart 상품 정보
		int i = 0;
		for(ItemSet is: itemList) {
			int seq = ++ i;
			SaleItem saleItem =
					new SaleItem(sale.getSaleid(),seq,is);
			sale.getItemList().add(saleItem);	//Sale 객체의 SaleItem 추가
			saleItemDao.insert(saleItem);
		}
		return sale;  
	}

	public List<Sale> salelist(String id) {
		return saleDao.list(id);	//사용자 id
	}

	public List<SaleItem> saleItemList(int saleid) {
		return saleItemDao.list(saleid);	//saleid 주문번호
	}

	public void update(User user) {
		userDao.update(user);
	}

	public void delete(String userid) {
		userDao.delete(userid);
	}

	public List<User> getUserList() {
		return userDao.list();
	}

	public List<User> userList(String[] idchks) {
		return userDao.list(idchks);
	}

	public void boardWrite(Board board, HttpServletRequest request) {
		if(board.getFile1() != null && !board.getFile1().isEmpty()) {
			uploadFileCreate(board.getFile1(), request, "board/imgfile/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		int max = boardDao.maxnum();
		board.setNum(++max);
		board.setGrp(max);
		boardDao.insert(board); 
	}

	public int boardCount(String searchtype, String searchcontent) {
		map.clear();
		if(searchtype != null) {
			String[] cols = searchtype.split(",");
			map.put("col1", cols[0]);
			if(cols.length > 1) {
				map.put("col2", cols[1]);
			}
			if(cols.length > 2) {
				map.put("col3", cols[2]);
			}
		}
		map.put("searchcontent", searchcontent);
		return boardDao.count(searchtype,searchcontent);
	}

	public List<Board> boardlist(Integer pageNum, int limit, String searchtype, String searchcontent) {
		return boardDao.list(pageNum,limit,searchtype,searchcontent);
	}

	public Board selectOne(int num) {
		return boardDao.detail(num);
	}

	public void readcnt(int num) {
		boardDao.readcnt(num);
	}

	public Board getBoard(Integer num, boolean able) {
		if(able) {
			boardDao.readcnt(num);
		}
		return boardDao.detail(num);
	}

	/*
	 *  답변글 데이터 추가 => service에서 처리
	 * 	- grp에 해당하는 레코드 grpstep 값보다 큰 grpstep의 값을 grpstep +1
	 * 	- maxnum + 1 값으로 num 값을 저장
	 * 	- grplevel + 1 값으로 grplevel 값을 저장
	 * 	- grpstep + 1 값으로 grpstep 값을 저장
	 * 	- 파라미터값으로 board 테이블에 insert하기
	 */
	public void reply(Board board) {
		boardDao.updateGrpStep(board);	//기존의 답글 정보의 grpstep을 증가.
		int max = boardDao.maxnum();
		//답글 정보 수정
		board.setNum(++max);
		board.setGrplevel(board.getGrplevel()+1);
		board.setGrpstep(board.getGrpstep()+1);
		boardDao.insert(board);
		
		System.out.println(++max);
		System.out.println(board.getGrplevel()+1);
		System.out.println(board.getGrpstep()+1);
	}

	public void boardUpdate(Board board, HttpServletRequest request) {
		//업로드되는 파일이 존재하는 경우;
		if(board.getFile1() != null && !board.getFile1().isEmpty()) {
			uploadFileCreate(board.getFile1(), request, "board/imgfile/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		boardDao.update(board); 
	}

	public void boardDelete(Board board) {
		boardDao.delete(board);
	}

	public Map<String, Object> graph1() {
		Map<String,Object> map = new HashMap<String,Object>();
		for(Map<String,Object> m : boardDao.graph1()) {
			map.put((String)m.get("name"), m.get("cnt"));
		}
		return map;
	}

	public Map<String, Object> graph2() {
		Map<String,Object> map = new HashMap<String,Object>();
		for(Map<String,Object> m : boardDao.graph2()) {
			map.put((String)m.get("date"), m.get("cnt"));
		}
		return map;
	}


	

	
}
