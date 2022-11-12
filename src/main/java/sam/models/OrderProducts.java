/** @author Sam Liew 12 Nov 2022 12:06:07 AM */
package sam.models;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_products")
public class OrderProducts {
	
	@Id
	@Column(name="order_products_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger orderProductsId;

	@Column(name="ref_product_id")
	private BigInteger refProductId;
	
	public BigInteger getOrderProductsId() {
		return orderProductsId;
	}

	public void setOrderProductsId(BigInteger orderProductsId) {
		this.orderProductsId = orderProductsId;
	}

	public BigInteger getRefProductId() {
		return refProductId;
	}

	public void setRefProductId(BigInteger refProductId) {
		this.refProductId = refProductId;
	} 
	
	

}
